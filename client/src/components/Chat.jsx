import React, { useState, useEffect, useRef, useCallback } from 'react';
import DOMPurify from 'dompurify';
import './Chat.css';

const Chat = ({ client, roomId, username, canChat, width, height }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [isAtBottom, setIsAtBottom] = useState(true);
  const [showScrollButton, setShowScrollButton] = useState(false);
  const [error, setError] = useState(null);
  const [unreadCount, setUnreadCount] = useState(0);

  const chatWindowRef = useRef(null);

  useEffect(() => {
    if (!client || !client.connected || !roomId) return;
    const subscription = client.subscribe(`/topic/room/${roomId}/chat`, (message) => {
      const chatMessage = JSON.parse(message.body);
      setMessages((prev) => [...prev, chatMessage]);
      if (!isAtBottom) {
        setShowScrollButton(true);
        setUnreadCount((prevCount) => prevCount + 1);
      }
    });
    
    return () => {
      subscription.unsubscribe();
    };
  }, [client, roomId, isAtBottom]);

  useEffect(() => {
    if (isAtBottom && chatWindowRef.current) {
      chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight;
    }
  }, [messages, isAtBottom]);

  useEffect(() => {
    const chatWindow = chatWindowRef.current;
    if (!chatWindow) return;
    const handleScroll = () => {
      const { scrollTop, scrollHeight, clientHeight } = chatWindow;
      const atBottom = scrollHeight - scrollTop - clientHeight < 100;
      setIsAtBottom(atBottom);
      if (atBottom) {
        setShowScrollButton(false);
        setUnreadCount(0);
      }
    };
    chatWindow.addEventListener('scroll', handleScroll);
    return () => {
      chatWindow.removeEventListener('scroll', handleScroll);
    };
  }, []);

  const scrollToBottom = useCallback(() => {
    if (!chatWindowRef.current) return;
    chatWindowRef.current.scrollTo({
      top: chatWindowRef.current.scrollHeight,
      behavior: 'smooth',
    });
    setShowScrollButton(false);
    setIsAtBottom(true);
    setUnreadCount(0);
  }, []);

  const handleSendMessage = () => {
    if (!client || !roomId || newMessage.trim() === '') return;
    const messageData = {
      text: newMessage,
      sender: username,
      type: 'user',
    };
    client.publish({
      destination: `/app/room/${roomId}/chat`,
      body: JSON.stringify(messageData),
    });
    setNewMessage('');
  };

  return (
    <div className="chat-container" style={{ height: `${height}px` }}>
      <div className="chat-header">
        <h2>Chat</h2>
      </div>
      <div className="chat-window" ref={chatWindowRef}>
        {messages.map((message, index) => {
          const sanitizedText = DOMPurify.sanitize(message.text);
          return (
            <div
              key={index}
              className={`chat-message ${message.type === 'system' ? 'system-message' : 'user-message'}`}
            >
              {message.type === 'system' ? (
                <span className="chat-text system-text" dangerouslySetInnerHTML={{ __html: sanitizedText }} />
              ) : (
                <>
                  <span className="chat-sender">{message.sender}: </span>
                  <span className="chat-text" dangerouslySetInnerHTML={{ __html: sanitizedText }} />
                </>
              )}
            </div>
          );
        })}
      </div>
      {showScrollButton && (
        <button
          className="scroll-button"
          onClick={scrollToBottom}
          aria-label="Scroll to the latest messages"
        >
          ↓ {unreadCount > 0 ? `${unreadCount} New Messages` : 'New Messages'}
        </button>
      )}
      <div className="chat-input">
        {error && <div className="error-message">{error}</div>}
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Type a message..."
          onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
          disabled={!canChat}
          aria-label="Type your message"
        />
        <button onClick={handleSendMessage} disabled={!canChat}>
          Send
        </button>
      </div>
    </div>
  );
};

export default Chat;