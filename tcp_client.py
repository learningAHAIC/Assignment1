import socket

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect(('localhost', 12345))
    s.sendall(b'Hello, server!')
    data = s.recv(1024)
    print('Received:', data.decode())
    s.sendall(b'How are you?')
    data = s.recv(1024)
    print('Received:', data.decode())