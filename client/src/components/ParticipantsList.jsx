import React, { useState, useEffect, useRef } from 'react';
import './ParticipantsList.css';

const ParticipantsList = ({client,  height, roomId, username }) => {
  const [userList, setUserList] = useState([]);
  const participantsWindowRef = useRef(null);

  useEffect(() => {
    if (!client || !roomId) return;

    const subscription = client.subscribe(`/topic/room/${roomId}/participants`, (message) => {
      const participants = JSON.parse(message.body);
      setUserList(participants);
    });

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [client, roomId]);

  return (
    <div className="participants-container" style={{ height: `${height}px` }}>
      <div className="participants-header">
        <h2>Participants</h2>
      </div>
      <div className="participants-window" ref={participantsWindowRef}>
        {userList.map((user) => (
          <div key={user.socketID} className="participant-item">
            <span className="participant-name">{user.username}</span>
            {user.isDrawer && (
              <span className="drawer-indicator"> (Drawing)</span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ParticipantsList;
