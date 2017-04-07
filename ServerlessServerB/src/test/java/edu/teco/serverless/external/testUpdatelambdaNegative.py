import requests
import unittest
import json

class TestPositiveResultsUpdatelambda(unittest.TestCase):

    def setUp(self):
        print("=== Setup ===")
        self.name="update"
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"Python3", 
                    "libraries":[], 
                    "code":"print(\"Hello world\")"
                }
        }
        payload['name'] = self.name
        header = {
                'Content-Type':'application/json'
        }
        r = requests.post('http://localhost:8080/lambdas', json = payload, headers = header)
        j = json.loads(r.text)
        self.token = j['token']

    def testPUTlambdaNoBody(self):
        print('== Test: No body ==')
        r = requests.put('http://localhost:8080/lambdas/update')
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaEmptyBody(self):
        print('== Test: Empty body ==')
        payload = {}
        r = requests.put('http://localhost:8080/lambdas/update', data = {})
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaEmptyBodyValues(self):
        print('== Test: Empty body values ==')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaMissingPayloadArgumentName(self):
        print('Test: Missing Payload Argument Name')
        payload = {
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaMissingPayloadArgumentRuntimeAttributes(self):
        print('Test: Missing Payload Argument RuntimeAttributes')
        payload = {
                "name":""
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaMissingPayloadArgumentLanguage(self):
        print('Test: Missing Payload Argument Language')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaMissingPayloadArgumentLibraries(self):
        print('Test: Missing Payload Argument Libraries')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"", 
                    "code":""
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaMissingPayloadArgumentCode(self):
        print('Test: Missing Payload Argument Code')
        payload = {
                "name":"",
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaGlibberishPayloadArgument(self):
        print('Test: Glibberish Payload Argument')
        payload = {
                'jaklfjdlkfjlkfjslk':"askdld"
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaRandomPayloadArgument(self):
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
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaEmptyRuntimeAttributes(self):
        print('Test: Empty RuntimeAttributes')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))
   
    def testPUTlambdaEmptyLanguage(self):
        print('Test: Empty Language')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                    "language":"", 
                    "libraries":[], 
                    "code":"print(\"Hello world bro\")"
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))

    def testPUTlambdaEmptyCode(self):
        print('Test: Empty Code')
        payload = {
                "name":"test",
                "runtimeAttributes":{
                    "language":"Python3", 
                    "libraries":[], 
                    "code":""
                }
        }
        r = requests.put('http://localhost:8080/lambdas/update', data = payload)
        self.assertIn("message", json.loads(r.text))


    def tearDown(self):
        print("=== Cleanup ===")
        auth = "Bearer " + self.token
        header = {
                'Content-Type':'application/json',
                'Authorization':''
        }
        header['Authorization'] = auth
        r = requests.delete('http://localhost:8080/lambdas/' + self.name, headers = header)

if __name__ == '__main__':
    print('===== TEST Positive Results for PUT /lambda/concrete =====')
    unittest.main()
