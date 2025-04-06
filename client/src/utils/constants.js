export const VIRTUAL_WIDTH = 10
export const VIRTUAL_HEIGHT = 16
export const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT
export const DEFAULT_SOCKET_URL = "http://localhost:8080/draw-and-guess"
export const DEFAULT_LEADERBOARD_URL = "http://localhost:8080/leaderboard"
export const LEADERBOARD_REFRESH_INTERVAL = 100
export const MAX_NICKNAME_LENGTH = 20
export const MAX_ROOM_NAME_LENGTH = 20
export const MAX_WINNER_MESSAGE_LENGTH = 50
export const CANVAS_WIDTH_RATIO = 0.9
export const CANVAS_HEIGHT_RATIO = 0.8
export const CHAT_HEIGHT_RATIO = 0.7
export const PARTICIPANTS_HEIGHT_RATIO = 0.3
export const FIRST_PLACE_ICON = "🏆"
export const SECOND_PLACE_ICON = "🥈"
export const THIRD_PLACE_ICON = "🥉"

export const APP_GET_ROOMS = '/app/getRooms'
export const APP_CREATE_ROOM = '/app/createRoom'
export const APP_JOIN_ROOM = '/app/joinRoom'
export const APP_GET_CURRENT_HINT = (roomId) => `/app/room/${roomId}/getCurrentHint`
export const APP_REQUEST_WORDS = (roomId) => `/app/room/${roomId}/requestWords`
export const APP_CHOOSE_WORD = (roomId) => `/app/room/${roomId}/chooseWord`
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


