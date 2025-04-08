import React, { useState, useEffect, useRef, useCallback } from 'react'
import { useSelector } from 'react-redux'
import DOMPurify from 'dompurify'
import { SYSTEM_MESSAGE_COLORS, CHAT_TITLE, SCROLL_BUTTON_LABEL, NEW_MESSAGES_LABEL, CHAT_PLACEHOLDER, SEND_BUTTON_TEXT } from '../../utils/constants'
import { TOPIC_ROOM_CHAT, APP_ROOM_CHAT } from '../../utils/subscriptionConstants'
import WinnerPrompt from '../Prompt/WinnerPrompt'
import './Chat.css'

const Chat = ({client, height }) => {
  const [messages, setMessages] = useState([])
  const [newMessage, setNewMessage] = useState('')
  const [isAtBottom, setIsAtBottom] = useState(true)
  const [showScrollButton, setShowScrollButton] = useState(false)
  const [unreadCount, setUnreadCount] = useState(0)
  const [showWinnerPrompt, setShowWinnerPrompt] = useState(false)
  const chatWindowRef = useRef(null)
  const sessionId = useSelector(state => state.user.sessionId)
  const roomId = useSelector(state => state.room.room?.roomId)
  const username = useSelector(state => state.user.username)
  const isDrawer = useSelector(state => state.game.isDrawer)
  function getSystemMessageColor(messageType) {
    return SYSTEM_MESSAGE_COLORS[messageType] || 'gray'
  }
  
  useEffect(() => {
    if (!client || !client.connected || !roomId) return
    const subscription = client.subscribe(TOPIC_ROOM_CHAT(roomId), (message) => {
      const chatMessage = JSON.parse(message.body)
      setMessages((prev) => [...prev, chatMessage])
      if (!isAtBottom) {
        setShowScrollButton(true)
        setUnreadCount((prevCount) => prevCount + 1)
      }
      if (chatMessage.senderSessionId === 'system' && chatMessage.messageType === 'WINNER_ANNOUNCED') {
        if (chatMessage.winnerSessionId === sessionId) {
          setShowWinnerPrompt(true)
        }
      }
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, roomId, isAtBottom, sessionId])

  useEffect(() => {
    if (isAtBottom && chatWindowRef.current) {
      chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight
    }
  }, [messages, isAtBottom])

  useEffect(() => {
    const chatWindow = chatWindowRef.current
    if (!chatWindow) return
    const handleScroll = () => {
      const { scrollTop, scrollHeight, clientHeight } = chatWindow
      const atBottom = scrollHeight - scrollTop - clientHeight < 100
      setIsAtBottom(atBottom)
      if (atBottom) {
        setShowScrollButton(false)
        setUnreadCount(0)
      }
    }
    chatWindow.addEventListener('scroll', handleScroll)
    return () => {
      chatWindow.removeEventListener('scroll', handleScroll)
    }
  }, [])

  const scrollToBottom = useCallback(() => {
    if (!chatWindowRef.current) return
    chatWindowRef.current.scrollTo({
      top: chatWindowRef.current.scrollHeight,
      behavior: 'smooth'
    })
    setShowScrollButton(false)
    setIsAtBottom(true)
    setUnreadCount(0)
  }, [])

  const handleSendMessage = () => {
    if (!client || !roomId || newMessage.trim() === '' || !sessionId) return
    const messageData = {
      text: newMessage,
      senderSessionId: sessionId,
      senderUsername: username,
      type: 'user'
    }
    client.publish({
      destination: APP_ROOM_CHAT(roomId),
      body: JSON.stringify(messageData)
    })
    setNewMessage('')
  }

  return (
    <div className="chat-container" style={{ height: `${height}px` }}>
      <div className="chat-header">
        <h2>{CHAT_TITLE}</h2>
      </div>
      <div className="chat-window" ref={chatWindowRef}>
        {messages.map((message, index) => {
          const sanitizedText = DOMPurify.sanitize(message.text)
          if (message.senderSessionId === 'system') {
            return (
              <div
                key={index}
                className="chat-message system-message"
              >
                <span
                  className="chat-text system-text"
                  style={{ color: getSystemMessageColor(message.messageType) }}
                  dangerouslySetInnerHTML={{ __html: sanitizedText }}
                />
              </div>
            )
          }
          return (
            <div
              key={index}
              className="chat-message user-message"
            >
              <span className="chat-sender">{message.senderUsername}: </span>
              <span className="chat-text" dangerouslySetInnerHTML={{ __html: sanitizedText }} />
            </div>
          )
        })}
      </div>
      {showScrollButton && (
        <button
          className="scroll-button"
          onClick={scrollToBottom}
          aria-label={SCROLL_BUTTON_LABEL}
        >
          ↓ {unreadCount > 0 ? `${unreadCount} ${NEW_MESSAGES_LABEL}` : NEW_MESSAGES_LABEL}
        </button>
      )}
      <div className="chat-input">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder={CHAT_PLACEHOLDER}
          onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
          disabled={isDrawer}
          aria-label="Type your message"
        />
        <button onClick={handleSendMessage} disabled={isDrawer}>
          {SEND_BUTTON_TEXT}
        </button>
      </div>
      {showWinnerPrompt && (
        <WinnerPrompt
          username={username}
          client={client}
          connected={client && client.connected}
          onClose={() => setShowWinnerPrompt(false)}
        />
      )}
    </div>
  )
}

export default Chat
