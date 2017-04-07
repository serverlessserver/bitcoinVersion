from request_utils import UploadRequest, ExecuteRequest, DeleteRequest
import requests
import unittest

class TestHTTPERROR401Unauthorized(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        result = UploadRequest("hello", "Python3", [], "print('Hello world')").send()
        TestHTTPERROR401Unauthorized.token = result["token"]
    
    def testGETlambdas(self):
        print('=== Test: GET /lambdas===' )
        r = requests.get('http://localhost:8080/lambdas')
        self.assertEqual(r.status_code, 401)

    def testGETrandom(self):
        print('=== Test: GET /random===' )
        r = requests.get('http://localhost:8080/random')
        self.assertEqual(r.status_code, 401)

    def testGETlambdaconcrete(self):
        print('=== Test: GET /lambdas/concrete ===' )
        r = requests.get('http://localhost:8080/lambdas/concrete')
        self.assertEqual(r.status_code, 401)

    def testGETroot(self):
        print('=== Test: GET / ===' )
        r = requests.get('http://localhost:8080/')
        self.assertEqual(r.status_code, 401)

    def testGETlambdaconcretetoken(self):
        print('=== Test: GET /lambdas/concrete/token ===' )
        r = requests.get('http://localhost:8080/lambdas/concrete/token')
        self.assertEqual(r.status_code, 401)

    def testExecuteInvalidToken(self):
        result = ExecuteRequest("hello", "not_a_valid_token", 1, []).send()
        self.assertEqual(result["message"], "Authentication Failed: Invalid JWT.")

    @classmethod
    def tearDownClass(self):
        DeleteRequest("hello", TestHTTPERROR401Unauthorized.token).send()

if __name__ == '__main__':
    print('===== TEST HTTP ERROR 401 Unauthorized =====')
    unittest.main()
