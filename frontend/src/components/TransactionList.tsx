import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Trash2, Clock, ListOrdered } from 'lucide-react';
import { cn } from '@/lib/utils';

interface Transaction {
  id: string;
  valor: number;
  dataHora: string;
  isRecent: boolean;
}

interface TransactionListProps {
  transactions: Transaction[];
  onClear: () => void;
  totalCount: number;
}

export function TransactionList({ transactions, onClear, totalCount }: TransactionListProps) {
  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);

  const formatDateTime = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }).format(date);
  };

  return (
    <Card className="shadow-card border-border/50 animate-slide-up" style={{ animationDelay: '0.1s' }}>
      <CardHeader className="pb-4 flex flex-row items-center justify-between">
        <CardTitle className="text-lg font-semibold flex items-center gap-2">
          <div className="h-8 w-8 rounded-lg bg-secondary flex items-center justify-center">
            <ListOrdered className="h-4 w-4 text-secondary-foreground" />
          </div>
          Transações
          <span className="text-sm font-normal text-muted-foreground">
            ({totalCount} total)
          </span>
        </CardTitle>
        {totalCount > 0 && (
          <Button
            variant="outline"
            size="sm"
            onClick={onClear}
            className="text-destructive hover:text-destructive hover:bg-destructive/10"
          >
            <Trash2 className="h-4 w-4" />
            Limpar
          </Button>
        )}
      </CardHeader>
      <CardContent>
        {transactions.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            <Clock className="h-12 w-12 mx-auto mb-3 opacity-30" />
            <p className="text-sm">Nenhuma transação registrada</p>
          </div>
        ) : (
          <ScrollArea className="h-[300px] pr-4">
            <div className="space-y-2">
              {transactions.map((transaction, index) => (
                <div
                  key={transaction.id}
                  className={cn(
                    "flex items-center justify-between p-3 rounded-lg border transition-all duration-300",
                    transaction.isRecent
                      ? "bg-accent/5 border-accent/20"
                      : "bg-secondary/50 border-transparent opacity-60"
                  )}
                  style={{ animationDelay: `${index * 0.05}s` }}
                >
                  <div className="flex items-center gap-3">
                    <div className={cn(
                      "h-2 w-2 rounded-full",
                      transaction.isRecent ? "bg-accent animate-pulse-subtle" : "bg-muted-foreground"
                    )} />
                    <div>
                      <p className="font-semibold text-sm">
                        {formatCurrency(transaction.valor)}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {formatDateTime(transaction.dataHora)}
                      </p>
                    </div>
                  </div>
                  {transaction.isRecent && (
                    <span className="text-xs font-medium text-accent bg-accent/10 px-2 py-0.5 rounded-full">
                      Últimos 60s
                    </span>
                  )}
                </div>
              ))}
            </div>
          </ScrollArea>
        )}
      </CardContent>
    </Card>
  );
}
