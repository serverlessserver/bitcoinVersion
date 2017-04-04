import os

if os.path.exists("test.txt"):
    print("already exists")
else:
    try:
        open("test.txt", "w").close()
        print("created")
    except:
        print("error")
