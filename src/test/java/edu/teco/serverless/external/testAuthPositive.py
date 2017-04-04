import requests
import unittest
from datetime import date, timedelta
import json

class TestAuth(unittest.TestCase):
    def testAGenerateToken(self):
        print("===Test Generate Token===")
        self.name="mastertoken"
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
        print(j)
        self.assertEqual(r.status_code, 201)

    def testBGenerateSubtoken(self):
        print("===Test Generate Subtoken===")
        self.name="subtoken"
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
        print(j)
        self.token = j['token']
        header = {
                'Content-Type':'application/json',
                'Authorization':''
        }
        header['Authorization'] = "Bearer " + self.token
        t = date.today() + timedelta(days=1)
        r = requests.get('http://localhost:8080/lambdas/'+ self.name +'/token?expiryDate=' + t.strftime('%Y-%m-%d %H-%M-%S'), headers = header)
        print(r.text)
        self.assertEqual(r.status_code, 200)

    def testCExecuteWithSubtoken(self):
        print("===Test Execute with Subtoken===")
        self.name="execute"
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
        print(j)
        self.token = j['token']
        header = { 
                'Content-Type':'application/json',
                'Authorization':''
        }   
        header['Authorization'] = "Bearer " + self.token
        t = date.today() + timedelta(days=1)
        r = requests.get('http://localhost:8080/lambdas/'+ self.name +'/token?expiryDate=' + t.strftime('%Y-%m-%d %H-%M-%S'), headers = header)
        print(r.text)
        self.subtoken = r.text
        header = { 
                'Content-Type':'application/json',
                'Authorization':''
        }   
        header['Authorization'] = "Bearer " + self.subtoken
        payload = {
                "times":"1",
                "parameters":[]
        }
        r = requests.post('http://localhost:8080/lambdas/'+ self.name + "/execute", json = payload, headers = header)
        print(r.text)
        self.assertEqual(r.status_code, 200)
        self.assertEqual(r.text, "{\"message\":\"Hello world\\n\"}")


    def testDExecuteWithMastertoken(self):
        print("===Test Execute with Mastertoken===")
        self.name="execute"
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
        print(j)
        self.token = j['token']
        header = { 
                'Content-Type':'application/json',
                'Authorization':''
        }   
        header['Authorization'] = "Bearer " + self.token
        payload = { 
                "times":"1",
                "parameters":[]
        }   
        r = requests.post('http://localhost:8080/lambdas/'+ self.name + "/execute", json = payload, headers = header)
        print(r.text)
        self.assertEqual(r.status_code, 200)
        self.assertEqual(r.text, "{\"message\":\"Hello world\\n\"}")

    def testEGetLambdaWithMastertoken(self):
        print("===Test Get lambda config with Mastertoken===")
        self.name="lambdaconfig"
        payload = { 
                "name":"",
                "runtimeAttributes":{
                    "language":"Python3", 
                    "libraries":[], 
                    #"code":"print(\"Hello world\")"
                    "code":"print(\"Hello world\")\n"
                }   
        }   
        payload['name'] = self.name
        header = { 
                'Content-Type':'application/json'
        }   
        r = requests.post('http://localhost:8080/lambdas', json = payload, headers = header)
        j = json.loads(r.text)
        print(j)
        self.token = j['token']
        header = { 
                'Content-Type':'application/json',
                'Authorization':''
        }   
        header['Authorization'] = "Bearer " + self.token
        r = requests.get('http://localhost:8080/lambdas/' + self.name, headers = header)
        j = json.loads(r.text)
        print(j)
        self.assertEqual(j, payload)

    def testFDeleteLambdaWithMastertoken(self):
        print("===Test Delete with Mastertoken===")
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
        print(j)
        self.token = j['token']


    def testGUpdateLambdaWithMastertoken(self):
        print("===Test Update with Mastertoken===")
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
        print(j)
        self.token = j['token'] 
        payload['code'] = "print(\"Hello\ world updated\")"
        header = { 
                'Content-Type':'application/json',
                'Authorization':''
        }   
        header['Authorization'] = "Bearer " + self.token
        r = requests.put('http://localhost:8080/lambdas/update'  , json = payload, headers = header)
        print(r)
        self.assertEqual(r.status_code, 200)

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
    print('===== TEST Auth=====')
    unittest.main()
