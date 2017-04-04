from request_utils import UploadRequest, GetLambdaRequest, DeleteRequest
import unittest

class TestGetLambdaPositive(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        name = "get"
        result = UploadRequest(name, "Python3", [], "print('Hello world')").send()
        TestGetLambdaPositive.lambdaTest = (name, result["token"])

    def testGetLambda(self):
        name = TestGetLambdaPositive.lambdaTest[0]
        token = TestGetLambdaPositive.lambdaTest[1]
        result = GetLambdaRequest(name, token).send()
        self.assertIn("name", result)

    @classmethod
    def tearDownClass(self):
        name = TestGetLambdaPositive.lambdaTest[0]
        token = TestGetLambdaPositive.lambdaTest[1]
        DeleteRequest(name, token).send()
    
if __name__ == '__main__':
    print('===== TEST Positive Results for GET /lambda/concrete =====')
    unittest.main()
