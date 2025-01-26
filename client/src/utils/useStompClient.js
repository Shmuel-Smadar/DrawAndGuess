import { useState, useEffect } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export default function useStompClient(baseUrl) {
  const [client, setClient] = useState(null)
  const [connected, setConnected] = useState(false)

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: baseUrl.replace('http', 'ws'),
      webSocketFactory: () => new SockJS(baseUrl),
      debug: (str) => console.log(str),
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
