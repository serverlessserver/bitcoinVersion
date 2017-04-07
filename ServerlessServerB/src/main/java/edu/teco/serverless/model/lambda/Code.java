package edu.teco.serverless.model.lambda;

/**
 * Contains code of lambda function as a string line.
 */
public class Code {
    private String code;

    public Code(String code) {

        this.code = code;
    }

    @Override
    public String toString() {
        return "Code{" +
                "code='" + code + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code)) return false;

        Code code1 = (Code) o;

        return getCode().equals(code1.getCode());

    }

    @Override
    public int hashCode() {
        return getCode().hashCode();
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
