
import React from 'react';
import Chat from './Chat';
import ParticipantsList from './ParticipantsList';
import './RightSidebar.css';

const RightSidebar = ({
  client,
  roomId,
  username,
  canChat,
  width,
  height,
  onDrawerChange
}) => {
  const chatHeight = height * 0.75;
  const participantsHeight = height * 0.25;
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
      <ParticipantsList
        client={client}
        height={participantsHeight}
        roomId={roomId}
        username={username}
        onDrawerChange={onDrawerChange}
      />
    </div>
  );
};

export default RightSidebar;