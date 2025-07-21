import { useEffect, useCallback } from 'react';
import { useDispatch } from 'react-redux';
import { 
  setIsDrawer,
  setShowWordSelection, 
  setWordOptions 
} from '../store/gameSlice';
import { 
  APP_REQUEST_WORDS, 
  APP_CHOOSE_WORD,
  USER_TOPIC_WORD_OPTIONS,
  TOPIC_ROOM_CHAT 
} from '../utils/subscriptionConstants';

/**
 * Custom hook to handle game-related WebSocket subscriptions
 */
const useGameSubscriptions = ({ client, connected, room, isDrawer }) => {
  const dispatch = useDispatch();

  const requestWordOptions = useCallback(() => {
    if (!client || !connected || !room) return;
    client.publish({
      destination: APP_REQUEST_WORDS(room.roomId),
      body: ''
    });
  }, [client, connected, room]);

  const handleWordSelect = (selectedWord) => {
    if (!client || !connected || !room) return;
    client.publish({
      destination: APP_CHOOSE_WORD(room.roomId),
      body: selectedWord
    });
    dispatch(setShowWordSelection(false));
  };

  const handleDrawerChange = (drawerState) => {
    if (drawerState !== isDrawer) {
      dispatch(setIsDrawer(drawerState));
      if (drawerState) {
        requestWordOptions();
      }
    }
  };

  // Subscribe to word options
  useEffect(() => {
    if (!client || !connected) return;
    
    const sub = client.subscribe(USER_TOPIC_WORD_OPTIONS, (msg) => {
      const data = JSON.parse(msg.body);
      dispatch(setWordOptions([data.word1, data.word2, data.word3]));
      dispatch(setShowWordSelection(true));
    });
    
    return () => sub.unsubscribe();
  }, [client, connected, dispatch]);

  // Subscribe to room chat for game events
  useEffect(() => {
    if (!client || !connected || !room) return;
    
    const chatSub = client.subscribe(TOPIC_ROOM_CHAT(room.roomId), (msg) => {
      const message = JSON.parse(msg.body);
      if (message.senderSessionId === 'system' && 
          message.messageType === 'NEW_GAME_STARTED' && 
          isDrawer) {
        requestWordOptions();
      }
    });
    
    return () => chatSub.unsubscribe();
  }, [client, connected, room, isDrawer, requestWordOptions]);

  return {
    requestWordOptions,
    handleWordSelect,
    handleDrawerChange
  };
};

export default useGameSubscriptions; 