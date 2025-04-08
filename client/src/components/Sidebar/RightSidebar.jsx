import React from 'react';
import Chat from './Chat';
import ParticipantsList from './ParticipantsList';
import './RightSidebar.css'
import { CHAT_HEIGHT_RATIO, PARTICIPANTS_HEIGHT_RATIO } from '../../utils/constants';

const RightSidebar = ({client, height, onDrawerChange }) => {
  const chatHeight = height * CHAT_HEIGHT_RATIO
  const participantsHeight = height * PARTICIPANTS_HEIGHT_RATIO

  return (
    <div className="right-sidebar" style={{ height: `${height}px` }}>
      <Chat
      client={client}
       height={chatHeight} />
      <ParticipantsList
        client={client}
        height={participantsHeight}
        onDrawerChange={onDrawerChange}
      />
    </div>
  );
};

export default RightSidebar;