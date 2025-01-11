import React from 'react';
import Chat from './Chat';
import './RightSidebar.css';

const RightSidebar = ({ client, roomId, username, canChat, width, height }) => {
  const chatHeight = height * 0.75;
  return (
    <div className="right-sidebar" style={{ height: `${height}px` }}>
      <Chat
        client={client}
        roomId={roomId}
        username={username}
        canChat={canChat}
        width={width}
        height={chatHeight}
      />
    </div>
  );
};

export default RightSidebar;