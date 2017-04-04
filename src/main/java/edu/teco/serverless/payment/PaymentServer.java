package edu.teco.serverless.payment;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.protobuf.InvalidProtocolBufferException;
import edu.teco.serverless.auth.exception.JwtMalformedException;
import org.apache.log4j.Logger;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;


/**
 * This class describes payment server for payment's processing, implemented as singleton.
 */
@Component
public class PaymentServer {
    final static Logger logger = Logger.getLogger(PaymentServer.class);

    /**
     * Needed payment's amount, which payment server accepts as valid
     */
    private final static int PRICE_OF_PRODUCT = 100000;

    /**
     * Simple name for file of payment server's wallet to be stored
     */
    private final static String NAME_OF_WALLET_FILE = "test-serverless-wallet";

    /**
     * How many time server need to wait before set transaction as valid.
     */
    private final static int CONFIDENCE = 1;

    /**
     * URL from where payment request from payment server must be fetched by user's bitcoin client.
     */
    private final static String PAYMENT_REQUEST_URL = "http://localhost:8080/lambdas/payment";

    /**
     * Provides test environment for payments, that is here are only virtual money.
     */
    private final static NetworkParameters ENVIRONMENT = TestNet3Params.get();

    /**
     * Wallet Kit for wallet managment
     */
    private WalletAppKit walletAppKit;

    /**
     * Needed payment's amount, which payment server accepts as valid, but in type ordinar for Wallet Kit
     */
    private Coin price;

    private static PaymentServer instance;

    private PaymentServer() {

        this.price = Coin.valueOf(PRICE_OF_PRODUCT);

        this.walletAppKit = new WalletAppKit(ENVIRONMENT, new File("."), NAME_OF_WALLET_FILE);
        this.walletAppKit.startAsync();
        this.walletAppKit.awaitRunning();

        init();
    }

    /**
     * Initiates listeners in order to provide some activities for incoming payments.
     */
    private void init() {
        this.walletAppKit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Coin value = tx.getValueSentToMe(w);
                logger.info("Received tx for " + value.toFriendlyString() + ": " + tx);
                logger.info("Waiting for confirmation of transaction by Bticoin's network...");

                Futures.addCallback(tx.getConfidence().getDepthFuture(CONFIDENCE), new FutureCallback<TransactionConfidence>() {
                    @Override
                    public void onSuccess(TransactionConfidence result) {
                        logger.info("success, baby, serverless have already received some money!!!!!!!!!!");
                        logger.info("New balance: " + walletAppKit.wallet().getBalance().toFriendlyString());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.info("Oops, transaction is NOT valid!");
                        // This kind of future can't fail, just rethrow in case something weird happens.
                        throw new RuntimeException(t);
                    }
                });
            }
        });
        logger.info("Current balance: " + walletAppKit.wallet().getBalance().toFriendlyString());
    }

    /**
     * singleton method
     * @return PaymentServer's instance
     */
    public static PaymentServer getInstance() {
        if (instance == null) return new PaymentServer();
        return instance;
    }


    /**
     * Check whether input amount of payment is valid (is equal to price was set).
     * @param paymentMessageEncoded encoded payment with paid amount of bitcoins.
     * @return if payment is valid, returns name of lambda function, for what payment is done.
     * @throws InvalidPaymentException if payment's amount is lower then price.
     */
    public String checkPayment(byte[] paymentMessageEncoded) throws InvalidPaymentException {
        Protos.Payment paymentMessageDecoded;
        try {
            paymentMessageDecoded = Protos.Payment.parseFrom(paymentMessageEncoded);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
        List<Transaction> transactions = PaymentProtocol.parseTransactionsFromPaymentMessage(ENVIRONMENT, paymentMessageDecoded);
        Coin amountOfPayment = Coin.valueOf(0);
        for (Transaction t : transactions) {
            this.walletAppKit.peerGroup().broadcastTransaction(t);
            logger.info("broadcast transaction");
            amountOfPayment = amountOfPayment.add(t.getValue(this.walletAppKit.wallet()));
        }
        if (amountOfPayment.isLessThan(this.price)) throw new InvalidPaymentException("Payment amount is too low");
        return paymentMessageDecoded.getMerchantData().toStringUtf8();
    }

    /**
     * Sends payment request to the bitcoin clien, when user clicks on the bitcoin link.
     * @param name name of lambda function for that payment request need to be generated.
     * @return payment request object
     */
    public Protos.PaymentRequest sendPaymentRequestToClient(String name) {
        Address address = this.walletAppKit.wallet().freshReceiveAddress();
        Protos.PaymentRequest paymentRequest = PaymentProtocol.createPaymentRequest(
                ENVIRONMENT,
                this.price,
                address,
                "API call payment is required",
                "http://localhost:8080/lambdas/paymentACK",
                name.getBytes()).build();
        return paymentRequest;
    }

    /**
     * Generates bitcoin link for payment for usage lambda function
     * @param nameOfLambda name of lambda function user must pay for
     * @return bitcoin link as string object
     */
    public String getBitcoinURIString(String nameOfLambda) {
        return BitcoinURI.convertToBitcoinURI(
                ENVIRONMENT,
                this.walletAppKit.wallet().freshReceiveAddress().toBase58(),
                this.price,
                "Serverless",
                "pay").concat("&r=").concat(PAYMENT_REQUEST_URL).concat("/").concat(nameOfLambda);
    }

    /**
     * Generates payment acknowledgement when user has paid. This acknowledgement contains token for usage of lambda function
     * @param paymentMessageEncoded payment message need to be copied for creation of acknowledgement
     * @param token token to be included in payment acknowledgement
     * @return payment acknowledgement with token.
     */
    public Protos.PaymentACK getPaymentACK(byte[] paymentMessageEncoded, String token) throws JwtMalformedException {

        if (token == null) throw new JwtMalformedException("Token is null");
        Protos.Payment paymentMessageDecoded;
        try {
            paymentMessageDecoded = Protos.Payment.parseFrom(paymentMessageEncoded);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }

        return PaymentProtocol.createPaymentAck(paymentMessageDecoded, "Thanks for payment, here is your token: " + token);
    }

    public WalletAppKit getWalletAppKit() {
        return walletAppKit;
    }
}

