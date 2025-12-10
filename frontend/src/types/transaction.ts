export interface Transaction {
  id: string;
  valor: number;
  dataHora: string;
  isRecent?: boolean;
}

export interface Statistics {
  count: number;
  sum: number;
  avg: number;
  min: number;
  max: number;
}
