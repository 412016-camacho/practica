export enum CellType {
  EMPTY = 'EMPTY',
  MONSTER = 'MONSTER',
  BONUS = 'BONUS'
}

export enum GameStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  WON = 'WON',
  LOST = 'LOST'
}

export interface Cell {
  index: number;
  type: CellType;
  value?: number;
}

export interface WorldList {
  id: number;
  name: string;
  description: string;
}

export interface WorldDetail {
  id: number;
  name: string;
  description: string;
  cells: Cell[];
}

export interface CreateGameRequest {
  worldId: number;
  initialLife: number;
  playerName: string;
  seed?: number;
}

export interface GameResponse {
  gameId: number;
  worldId: number;
  playerName: string;
  position: number;
  life: number;
  status: GameStatus;
  turnNumber: number;
}

export interface PlayTurnRequest {
  diceOverride?: number;
}

export interface TurnResponse {
  gameId: number;
  turnNumber: number;
  fromPosition: number;
  toPosition: number;
  dice: number;
  cell: Cell;
  deltaLife: number;
  lifeAfter: number;
  statusAfter: GameStatus;
}

export interface TurnHistoryItem {
  turnNumber: number;
  dice: number;
  fromPosition: number;
  toPosition: number;
  deltaLife: number;
  lifeAfter: number;
  statusAfter: GameStatus;
}

export interface TurnHistoryResponse {
  gameId: number;
  turns: TurnHistoryItem[];
}

export interface RankingItem {
  playerName: string;
  gameId: number;
  turnNumber: number;
  lifeAfter: number;
  score: number;
}

export interface RankingResponse {
  top: number;
  items: RankingItem[];
}

export interface ErrorDetail {
  field: string;
  issue: string;
}

export interface ErrorApi {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  details?: ErrorDetail[];
}
