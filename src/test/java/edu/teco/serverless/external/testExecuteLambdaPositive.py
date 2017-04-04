from request_utils import ExecuteRequest, UploadRequest, DeleteRequest
import unittest

class TestExecutePositive(unittest.TestCase):
    @classmethod
    def setUpClass(self):
        TestExecutePositive.lambdas = []

    def testExecuteSimpleHelloWorld(self):
        # upload
        result = UploadRequest("simplehelloworld", "Python3", [], "print('Hello world')").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("simplehelloworld", token))

        # execute
        result = ExecuteRequest("simplehelloworld", token, 1, []).send()
        self.assertEqual("Hello world\n", result["message"])

    def testExecuteWith1Library(self):
        # upload
        result = UploadRequest("library", "Python3", ["numpy"], "import numpy as np\na = np.zeros((2,2))\nprint(a)").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("library", token))

        # execute
        result = ExecuteRequest("library", token, 1, []).send()
        self.assertEqual(result["message"], "[[ 0.  0.]\n [ 0.  0.]]\n")

    def testExecuteWith2Library(self):
        # upload
        result = UploadRequest("libraries", "Python3", ["numpy", "scipy"], "import numpy as np\nfrom scipy.special import gamma\n" +
                "print(np.array([1, 2, 3]))\nprint(gamma(0.5))").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("libraries", token))

        # execute
        result = ExecuteRequest("libraries", token, 1, []).send()
        self.assertEqual(result["message"], "[1 2 3]\n1.77245385091\n")

    def testExecute5times(self):
        # upload
        result = UploadRequest("five", "Python3", [], "print('Hello world')").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("five", token))

        # execute
        result = ExecuteRequest("five", token, 5, []).send()
        self.assertEqual("Hello world\nHello world\nHello world\nHello world\nHello world\n", result["message"])

    def testExecute20times(self):
        # upload
        result = UploadRequest("twenty", "Python3", [], "print('Hello world')").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("twenty", token))

        # execute
        result = ExecuteRequest("twenty", token, 20, []).send()
        self.assertEqual("Hello world\nHello world\nHello world\nHello world\nHello world\n"
                          "Hello world\nHello world\nHello world\nHello world\nHello world\n"
                          "Hello world\nHello world\nHello world\nHello world\nHello world\n"
                          "Hello world\nHello world\nHello world\nHello world\nHello world\n", result["message"])

    def testExecuteWithParameters(self):
        # upload
        result = UploadRequest("para", "Python3", [], "import sys\nprint(sys.argv[1], sys.argv[2])").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("para", token))

        # execute
        result = ExecuteRequest("para", token, 1, ["1", "2"]).send()
        self.assertEqual("1 2\n", result["message"])

    def testExecute5timesWithParameters(self):
        # upload
        result = UploadRequest("parafive", "Python3", [], "import sys\nprint(sys.argv[1], sys.argv[2])").send()
        token = result["token"]
        TestExecutePositive.lambdas.append(("parafive", token))

        # execute
        result = ExecuteRequest("parafive", token, 5, ["1", "2"]).send()
        self.assertEqual("1 2\n1 2\n1 2\n1 2\n1 2\n", result["message"])

    @classmethod
    def tearDownClass(self):
        for name, token in TestExecutePositive.lambdas:
            DeleteRequest(name, token).send()

    
if __name__ == '__main__':
    print('===== TEST Positive Results for POST /lambda/concrete =====')
    unittest.main()
