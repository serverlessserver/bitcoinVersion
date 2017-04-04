from request_utils import UploadRequest, DeleteRequest, UpdateRequest
import unittest

class TestUpdatelambdaPositive(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        #upload the standard function
        name = "update"
        request = UploadRequest(name, "Python3", [], "print('Hello world')")
        result = request.send()
        TestUpdatelambdaPositive.name = "update"
        TestUpdatelambdaPositive.token = result["token"]



    def testUpdateLibraries(self):
        print("=== testUpdateLibraries ===")
        name = "update"
        request = UpdateRequest(name, self.token, "Python3", ["numpy"], "print('Hello world')")
        result = request.send()
        self.assertIn("token", result)
        TestUpdatelambdaPositive.token = result["token"]

    def testUpdateCode(self):
        print("=== testUpdateLibraries ===")
        name = "update"
        request = UpdateRequest(name, self.token, "Python3", [], "print('Hello world hi')")
        result = request.send()
        self.assertIn("token", result)
        TestUpdatelambdaPositive.token = result["token"]

    def testUpdateNothing(self):
        print("=== testUpdateLanguage ===")
        name = "update"
        request = UpdateRequest(name, self.token, "Python3", [], "print('Hello world')")
        result = request.send()
        self.assertIn("token", result)
        TestUpdatelambdaPositive.token = result["token"]

    def testUpdateName(self):
        print("=== testUpdateLanguage ===")
        request = UpdateRequest("update", self.token, "Python3", [], "print('Hello world')", "anothername")
        result = request.send()
        self.assertIn("token", result)
        TestUpdatelambdaPositive.token = result["token"]
        TestUpdatelambdaPositive.name = "anothername"
        
    @classmethod
    def tearDownClass(self):
        DeleteRequest(TestUpdatelambdaPositive.name, TestUpdatelambdaPositive.token).send()
        

if __name__ == '__main__':
    print('===== TEST Positive Results for PUT /lambda/concrete =====')
    unittest.main()
