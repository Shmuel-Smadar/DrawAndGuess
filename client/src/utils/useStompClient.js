import { useState, useEffect } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

/*
 * A custom hook that creates a STOMP client using SockJS,
 * attempts to connect on mount, and disconnects on unmount.
 */

export default function useStompClient(baseUrl) {
  const [client, setClient] = useState(null)
  const [connected, setConnected] = useState(false)

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: baseUrl.replace('http', 'ws'),
      webSocketFactory: () => new SockJS(baseUrl),
      onConnect: () => {
        setConnected(true)
      },
      onDisconnect: () => {
        setConnected(false)
      },
    })
    stompClient.activate()
    setClient(stompClient)
    return () => {
      stompClient.deactivate()
    }
  }, [baseUrl])

  return { client, connected }
}
