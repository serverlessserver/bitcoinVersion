import datetime
import json
import requests


class Request:
    def __init__(self):
        self.url = "http://localhost:8080/lambdas"
        self.header = {
            "Content-Type": "application/json"
        }


class UploadRequest(Request):
    def __init__(self, name, language, libraries, code):
        Request.__init__(self)
        self.payload = {
            "name": name,
            "runtimeAttributes": {
                "language": language,
                "libraries": libraries,
                "code": code
            }
        }

    def send(self):
        result = requests.post(self.url, json=self.payload, headers=self.header)
        return json.loads(result.text)


class ExecuteRequest(Request):
    def __init__(self, name, token, times=1, parameters=[]):
        Request.__init__(self)
        self.url += "/" + name + "/execute"
        self.header["Authorization"] = "Bearer " + token
        self.payload = {
            "times": times,
            "parameters": parameters
        }

    def send(self):
        result = requests.post(self.url, json=self.payload, headers=self.header)
        return json.loads(result.text)


class UpdateRequest(Request):
    def __init__(self, name, token, language, libraries, code, newname=None):
        Request.__init__(self)
        self.url += "/" + name
        self.header["Authorization"] = "Bearer " + token
        if newname is None:
            newname = name
        self.payload = {
            "name": newname,
            "runtimeAttributes": {
                "language": language,
                "libraries": libraries,
                "code": code
            }
        }

    def send(self):
        result = requests.put(self.url, json=self.payload, headers=self.header)
        return json.loads(result.text)


class DeleteRequest(Request):
    def __init__(self, name, token):
        Request.__init__(self)
        self.url += "/" + name
        self.header["Authorization"] = "Bearer " + token

    def send(self):
        result = requests.delete(self.url, headers=self.header)
        return result.status_code


class SubtokenRequest(Request):
    def __init__(self, name, token):
        Request.__init__(self)
        self.url += "/" + name + "/token"
        self.header["Authorization"] = "Bearer " + token
        expiration = datetime.date.today() + datetime.timedelta(days=1)
        self.url += "?expiryDate=" + expiration.strftime('%Y-%m-%d %H-%M-%S')

    def send(self):
        result = requests.get(self.url, headers=self.header)
        return json.loads(result.text)


class GetLambdaRequest(Request):
    def __init__(self, name, token):
        Request.__init__(self)
        self.url += "/" + name
        self.header["Authorization"] = "Bearer " + token

    def send(self):
        result = requests.get(self.url, headers=self.header)
        return json.loads(result.text)
