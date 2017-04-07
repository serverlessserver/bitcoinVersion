from request_utils import UploadRequest, ExecuteRequest, UpdateRequest, DeleteRequest, SubtokenRequest, GetLambdaRequest
import unittest

# Usage of Request classes
# UploadRequest(name, language, libraries, code)
# ExecuteRequest(name, token, times, parameters)
# UpdateRequest(name, token, language, libraries, code)
# DeleteRequest(name, token)
# SubtokenRequest(name, token,
# GetLambdaRequest(name, token)


class ExampleTest(unittest.TestCase):
    def testAll(self):
        code = "print(\"Hello world!\")"
        code_updated = "print(\"Bye world!\")"
        language = "Python3"
        libraries = []
        name = "hello"

        request = UploadRequest(name, language, libraries, code)
        result = request.send()
        token = result["token"]
        self.assertEquals(result["link"], "/lambdas/hello")

        request = ExecuteRequest(name, token)
        result = request.send()
        self.assertEquals(result["message"], "Hello world!\n")

        request = GetLambdaRequest(name, token)
        result = request.send()

        request = SubtokenRequest(name, token)
        result = request.send()

        request = UpdateRequest(name, token, language, libraries, code_updated)
        result = request.send()
        token = result["token"]

        request = DeleteRequest(name, token)
        result = request.send()
        
