import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import logo from './logo.svg';
import './App.css';

function App() {
  const [client, setClient] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/example', // Use WebSocket endpoint
      webSocketFactory: () => new SockJS('http://localhost:8080/example'),
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('Connected to STOMP');
        stompClient.subscribe('/topic/messages', (msg) => {
          setMessages((prev) => [...prev, msg.body]);
        });
      },
      onDisconnect: () => console.log('Disconnected from STOMP'),
    });

    stompClient.activate();
    setClient(stompClient);

    return () => stompClient.deactivate();
  }, []);

  const sendMessage = () => {
    if (client && input.trim()) {
      client.publish({ destination: '/app/sendMessage', body: input });
      setInput('');
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type message..."
        />
        <button onClick={sendMessage}>Send</button>
        <div>
          {messages.map((msg, idx) => (
            <p key={idx}>{msg}</p>
          ))}
        </div>
      </header>
    </div>
  );
}

export default App;
