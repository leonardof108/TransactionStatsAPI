export interface Transaction {
  id: string;
  valor: number;
  dataHora: string;
}

export interface Statistics {
  count: number;
  sum: number;
  avg: number;
  min: number;
  max: number;
}
