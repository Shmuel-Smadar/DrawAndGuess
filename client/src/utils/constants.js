export const VIRTUAL_WIDTH = 10
export const VIRTUAL_HEIGHT = 16
export const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT


export const APP_GET_ROOMS = '/app/getRooms'
export const APP_CREATE_ROOM = '/app/createRoom'
export const APP_JOIN_ROOM = '/app/joinRoom'
export const APP_GET_CURRENT_HINT = (roomId) => `/app/room/${roomId}/getCurrentHint`
export const USER_TOPIC_ROOM_CREATED = '/user/topic/roomCreated'
export const USER_TOPIC_NICKNAME = '/user/topic/nickname'
export const USER_TOPIC_WORD_OPTIONS = '/user/topic/wordOptions'

export const TOPIC_ROOMS = '/topic/rooms'
export const TOPIC_ROOM_CHAT = (roomId) => `/topic/room/${roomId}/chat`
export const TOPIC_ROOM_DRAWING = (roomId) => `/topic/room/${roomId}/drawing`
export const TOPIC_ROOM_CLEAR_CANVAS = (roomId) => `/topic/room/${roomId}/clearCanvas`
export const TOPIC_ROOM_PARTICIPANTS = (roomId) => `/topic/room/${roomId}/participants`
export const TOPIC_ROOM_WORD_HINT = (roomId) => `/topic/room/${roomId}/wordHint`


export const EVENT_TYPE_START = 'START'
export const EVENT_TYPE_DRAW = 'DRAW'
export const EVENT_TYPE_STOP = 'STOP'
export const EVENT_TYPE_FILL = 'FILL'

export const SYSTEM_MESSAGE_COLORS = {
  PARTICIPANT_JOINED: 'forestgreen',
  PARTICIPANT_LEFT: 'crimson',
  WORD_GUESSED: 'darkorange',
  NO_GUESS: 'mediumpurple',
  GAME_ENDED: 'darkslategray',
  PREVIOUS_DRAWER_QUIT: 'firebrick',
  ROUND_STARTED: 'royalblue',
  NEW_GAME_STARTED: 'seagreen'
}

export const COLOR_OPTIONS = [
  { code: '#000000' },
  { code: '#FF0000' },
  { code: '#00FF00' },
  { code: '#0000FF' },
  { code: '#FFFF00' },
  { code: '#A52A2A' },
  { code: '#800080' },
  { code: '#FFA500' },
  { code: '#FFC0CB' },
  { code: '#808080' },
  { code: '#00FFFF' },
  { code: '#FF00FF' },
  { code: '#8950F7' }
]

export const BRUSH_SIZES = [
  { name: 'S', size: 2 },
  { name: 'M', size: 5 },
  { name: 'L', size: 10 }
]
