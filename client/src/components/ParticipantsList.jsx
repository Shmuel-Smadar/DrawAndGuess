import React, { useState, useEffect, useRef } from 'react';
import './ParticipantsList.css';

const ParticipantsList = ({ client, height, roomId, username, onDrawerChange }) => {
  const [userList, setUserList] = useState([]);
  const participantsWindowRef = useRef(null);

  useEffect(() => {
    if (!client || !roomId) return;

    // Subscribe to the participants list
    const subscription = client.subscribe(`/topic/room/${roomId}/participants`, (message) => {
      const participants = JSON.parse(message.body);
      setUserList(participants);

      // Find the current user in the list of participants
      const me = participants.find((p) => p.username === username);
      console.log('in participantsList')
      console.log(me.isDrawer)
      if (me && me.isDrawer) {
        // Notify the parent of the change

        onDrawerChange(true);
      } else {
        onDrawerChange(false);
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, [client, roomId, username, onDrawerChange]);

  return (
    <div className="participants-container" style={{ height: `${height}px` }}>
      <div className="participants-header">
        <h2>Participants</h2>
      </div>
      <div className="participants-window" ref={participantsWindowRef}>
        {userList.map((user) => (
          <div key={user.sessionId} className="participant-item">
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