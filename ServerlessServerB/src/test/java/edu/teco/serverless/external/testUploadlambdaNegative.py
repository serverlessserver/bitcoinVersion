import requests
import unittest
import json

class TestUploadLambdaNegative(unittest.TestCase):
    
    def testPOSTlambdaNoBody(self):
        print('== Test: No body ==')
        r = requests.post('http://localhost:8080/lambdas')
        self.assertIn("message", json.loads(r.text))



    def testPOSTlambdaEmptyBody(self):
        print('== Test: Empty body ==')
        payload = {}
        r = requests.post('http://localhost:8080/lambdas', data = {})
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaEmptyBodyValues(self):
        print('== Test: Empty body values ==')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"",
                    "libraries":[],
                    "code":""
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaMissingPayloadArgumentName(self):
        print('Test: Missing Payload Argument Name')
        payload = {
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaMissingPayloadArgumentRuntimeAttributes(self):
        print('Test: Missing Payload Argument RuntimeAttributes')
        payload = {
                "name":""
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaMissingPayloadArgumentLanguage(self):
        print('Test: Missing Payload Argument Language')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaMissingPayloadArgumentLibraries(self):
        print('Test: Missing Payload Argument Libraries')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"", 
                    "code":""
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaMissingPayloadArgumentCode(self):
        print('Test: Missing Payload Argument Code')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[]
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaGlibberishPayloadArgument(self):
        print('Test: Glibberish Payload Argument')
        payload = {
                "jaklfjdlkfjlkfjslk":"askdld"
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaRandomPayloadArgument(self):
        print('Test: Random Payload Argument')
        payload = {
                "name":"test",
                "foooo":"bar",
                "runtimeAttributes":{
                    "language":"Python3", 
                    "libraries":[], 
                    "code":"print(\"Hello world bro\")"
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaEmptyRuntimeAttributes(self):
        print('Test: Empty RuntimeAttributes')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))
   
    def testPOSTlambdaEmptyLanguage(self):
        print('Test: Empty Language')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                    "code":"print(\"Hello world bro\")"
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPOSTlambdaEmptyCode(self):
        print('Test: Empty Code')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                    "language":"Python3", 
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.post('http://localhost:8080/lambdas', data = payload)
        self.assertIn("message", json.loads(r.text))
    

if __name__ == '__main__':
    print('===== TEST Negative Results for POST /lambda=====')
    unittest.main()
