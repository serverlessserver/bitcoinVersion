from request_utils import UploadRequest, DeleteRequest
import unittest

class TestUploadLambdaPositive(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        TestUploadLambdaPositive.lambdas = []

    def testUploadSimple(self):
        print("=== testUploadSimple ===")
        name = "hello"
        request = UploadRequest(name, "Python3", [], "print('Hello world')")
        result = request.send()
        self.assertIn("token", result)
        TestUploadLambdaPositive.lambdas.append((name, result["token"]))
        

    def testUploadWithLibraries(self):
        print("=== testUploadWithLibraries ===")
        name = "libs"
        request = UploadRequest(name, "Python3", ["numpy", "scipy"], "import numpy as np\n from scipy.spatial.distance import pdist, squareform\n"
                                                                                  "x = np.array([[0, 1], [1, 0], [2, 0]]) \n print (x)"
                                                                                  "d = squareform(pdist(x, 'euclidean'))\n print (d)")
        result = request.send()
        self.assertIn("token", result)
        TestUploadLambdaPositive.lambdas.append((name, result["token"]))

    @classmethod
    def tearDownClass(self):
        for name, token in TestUploadLambdaPositive.lambdas:
            DeleteRequest(name, token).send()
        
if __name__ == '__main__':
    print('===== TEST Positive Results for POST /lambda=====')
    unittest.main()
