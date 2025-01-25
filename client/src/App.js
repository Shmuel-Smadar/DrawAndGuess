import React, { useEffect, useState } from 'react'
import useStompClient from './utils/useStompClient'
import DrawingArea from './components/DrawingArea'
import NicknamePrompt from './components/NicknamePrompt'
import RoomPrompt from './components/RoomPrompt'
import RightSidebar from './components/RightSidebar'
import WordSelection from './components/WordSelection'
import './App.css';

function App() {
  const [username, setUsername] = useState('')
  const [nicknameError, setNicknameError] = useState('')
  const [room, setRoom] = useState(null)
  const { client, connected } = useStompClient('http://localhost:8080/draw-and-guess');
  const [isDrawer, setIsDrawer] = useState(false)
  const [showWordSelection, setShowWordSelection] = useState(false)
  const [wordOptions, setWordOptions] = useState([])


  const handleDrawerChange = (drawerState) => {
    if(isDrawer !== drawerState) { 
      setIsDrawer(drawerState);
      if (drawerState) {
        requestWordOptions();
      }
    }
  };

  const requestWordOptions = () => {
    if (!client || !connected || !room) return;
    client.publish({
      destination: `/app/room/${room.roomId}/requestWords`,
      body: ''
    });
  };

  useEffect(() => {
    if (!client || !connected) return;
    const sub = client.subscribe('/user/topic/wordOptions', (msg) => {
      const data = JSON.parse(msg.body);
      setWordOptions([data.word1, data.word2, data.word3]);
      setShowWordSelection(true);
    });
    return () => sub.unsubscribe();
  }, [client, connected]);

  const handleWordSelect = (selectedWord) => {
    if (!client || !connected || !room) return;
    client.publish({
      destination: `/app/room/${room.roomId}/chooseWord`,
      body: selectedWord
    });
    setShowWordSelection(false);
  };

  if (!username) {
    return (
      <NicknamePrompt
        client={client}
        connected={connected}
        setUsername={setUsername}
        setNicknameError={setNicknameError}
        error={nicknameError}
      />
    );
  }

  if (!room) {
    return (
      <RoomPrompt
        client={client}
        connected={connected}
        setRoom={setRoom}
      />
    );
  }

  return (
    <div className="app">
      <h1>What's Being Drawn?</h1>
      <div className="gameArea">
        {connected && client && (
          <DrawingArea
            client={client}
            userID={username}
            roomId={room.roomId}
            isDrawingAllowed={isDrawer}
          />
        )}
        <RightSidebar
          client={client}
          roomId={room.roomId}
          username={username}
          canChat={true}
          width={window.innerWidth * 0.9}
          height={window.innerHeight * 0.83}
          onDrawerChange={handleDrawerChange}
        />
      </div>
      {isDrawer && showWordSelection && (
        <WordSelection
          words={wordOptions}
          onWordSelect={handleWordSelect}
        />
      )}
    </div>
  );
}

export default App;