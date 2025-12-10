import { Header } from '@/components/Header';
import { StatsPanel } from '@/components/StatsPanel';
import { TransactionForm } from '@/components/TransactionForm';
import { TransactionList } from '@/components/TransactionList';
import { useTransactions } from '@/hooks/useTransactions';

const Index = () => {
  const { transactions, statistics, addTransaction, clearTransactions, totalCount } = useTransactions();

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-8 space-y-8">
        <StatsPanel statistics={statistics} />
        
        <div className="grid md:grid-cols-2 gap-6">
          <TransactionForm onSubmit={addTransaction} />
          <TransactionList 
            transactions={transactions} 
            onClear={clearTransactions}
            totalCount={totalCount}
          />
        </div>
      </main>
      
      <footer className="border-t border-border/50 py-6 mt-auto">
        <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
          <p>
            Implementação do desafio de programação — Endpoints: 
            <code className="mx-1 px-1.5 py-0.5 bg-secondary rounded text-xs">POST /transacao</code>
            <code className="mx-1 px-1.5 py-0.5 bg-secondary rounded text-xs">DELETE /transacao</code>
            <code className="mx-1 px-1.5 py-0.5 bg-secondary rounded text-xs">GET /estatistica</code>
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Index;
