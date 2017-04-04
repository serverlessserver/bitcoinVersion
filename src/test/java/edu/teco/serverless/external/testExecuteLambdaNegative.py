from request_utils import UploadRequest, ExecuteRequest, DeleteRequest
import requests
import unittest
import json

class TestExecuteNegative(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        result = UploadRequest("syntax", "Python3", [], "print('Hello world'").send()
        TestExecuteNegative.token_syntax = result["token"]
        result = UploadRequest("para", "Python3", [], "import sys\nprint(sys.argv[1])").send()
        TestExecuteNegative.token_para = result["token"]

    def testExecuteSyntaxError(self):
        result = ExecuteRequest("syntax", TestExecuteNegative.token_syntax, 1, []).send()
        self.assertEqual(result["message"], "Internal error.")

    def testExecuteParametersSyntaxError(self):
        result = ExecuteRequest("para", TestExecuteNegative.token_para, 1, []).send()
        self.assertEqual(result["message"], "Internal error.")

    @classmethod
    def tearDownClass(self):
        DeleteRequest("syntax", TestExecuteNegative.token_syntax).send()
        DeleteRequest("para", TestExecuteNegative.token_para).send()
    
if __name__ == '__main__':
    print('===== TEST Negative Results for POST /lambda/concrete =====')
    unittest.main()
