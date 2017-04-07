import requests
import unittest
import docker
import json

class TestDeleteLambdaNegative(unittest.TestCase):
    def testDeleteNotExistingLambda(self):
        result = DeleteRequest("imaginary", "just a token").send()
        self.assertIn("message", result)

if __name__ == '__main__':
    print('===== TEST Positive Results for DELETE /lambda/concrete =====')
    unittest.main()
