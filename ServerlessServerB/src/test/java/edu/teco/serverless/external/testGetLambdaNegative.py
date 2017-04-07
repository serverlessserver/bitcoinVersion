from request_utils import GetLambdaRequest
import unittest

class TestNegativeResultsGetlambda(unittest.TestCase):
    def testGetLambdaNotExisting(self):
        result = GetLambdaRequest("imaginary", "just a token").send()
        self.assertIn("message", result)
    
if __name__ == '__main__':
    print('===== TEST Negative Results for GET /lambda/concrete =====')
    unittest.main()
