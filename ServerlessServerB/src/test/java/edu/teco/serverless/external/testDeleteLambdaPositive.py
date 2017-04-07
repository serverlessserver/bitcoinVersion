from request_utils import UploadRequest, DeleteRequest
import unittest

class TestDeleteLambdaPositive(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        result = UploadRequest("delete", "Python3", [], "print('Hello world')").send()
        TestDeleteLambdaPositive.delete_token = result["token"]

    def testDelete(self):
        result = DeleteRequest("delete", TestDeleteLambdaPositive.delete_token).send()
        self.assertEquals(200, result)

if __name__ == '__main__':
    print('===== TEST Positive Results for DELETE /lambda/concrete =====')
    unittest.main()
