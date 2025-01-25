import React, { useState, useEffect } from 'react';
import './WordHint.css';

const WordHint = ({ client, roomId, isDrawer }) => {
  const [currentHint, setCurrentHint] = useState('');

  useEffect(() => {
    if (!client || !client.connected || isDrawer) return;

    const subscription = client.subscribe(`/topic/room/${roomId}/wordHint`, (message) => {
      const hint = message.body;
      console.log(hint)
      setCurrentHint(hint);
    });

    return () => {
      subscription.unsubscribe();
    };
  }, [client, roomId, isDrawer]);

  useEffect(() => {
      if (!client || !roomId) return;
  
      client.publish({
        destination: `/app/room/${roomId}/getCurrentHint`,
        body: ''
      });
    }, [client, roomId]);
  


  if (isDrawer) return null;

  return (
    <div className="word-hint-container">
      <h3>Word Hint:</h3>
      <div className="word-hint">
        {currentHint.split('').map((char, index) => (
          <span key={index} className="word-letter">
            {(char === ' ' ||char === '_') ? '\u00A0' : char}
          </span>
        ))}
      </div>
    </div>
  );
};

export default WordHint;