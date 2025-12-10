import { useState, useCallback, useEffect } from 'react';
import { Transaction, Statistics } from '@/types/transaction';
import { toast } from "sonner";

export function useTransactions() {
  // We keep a local list just for the UI "Recent List" visual,
  // but the math will come from the server.
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  // This state now comes from the Java Backend
  const [statistics, setStatistics] = useState<Statistics>({
    count: 0, sum: 0, avg: 0, min: 0, max: 0
  });

  // 1. POLL STATISTICS (The Heartbeat)
  // We fetch from Java every 1 second to see the numbers update
  const fetchStats = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:8080/estatistica');
      if (response.ok) {
        const data = await response.json();
        setStatistics(data);
      }
    } catch (error) {
      console.error("Failed to fetch stats from Java:", error);
    }
  }, []);

  useEffect(() => {
    fetchStats(); // Initial fetch
    const interval = setInterval(fetchStats, 1000); // Poll every 1s
    return () => clearInterval(interval);
  }, [fetchStats]);

  // 2. ADD TRANSACTION (POST)
  const addTransaction = useCallback(async (valor: number, dataHora: string) => {
    try {
      const response = await fetch('http://localhost:8080/transacao', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ valor, dataHora })
      });

      if (response.status === 201) {
        // Success: Update local UI list strictly for display purposes
        const newTransaction: Transaction = {
            id: crypto.randomUUID(),
            valor,
            dataHora,
        };
        setTransactions(prev => [newTransaction, ...prev]);

        // Force an immediate stats refresh
        fetchStats();
        return { success: true };
      } else if (response.status === 422) {
         return { success: false, error: 'Erro 422: Data futura ou valor inválido' };
      } else {
         return { success: false, error: `Erro ${response.status}: Falha no servidor` };
      }
    } catch (e) {
      return { success: false, error: 'Erro de conexão com o Backend' };
    }
  }, [fetchStats]);

  // 3. CLEAR TRANSACTIONS (DELETE)
  const clearTransactions = useCallback(async () => {
    try {
      await fetch('http://localhost:8080/transacao', { method: 'DELETE' });
      setTransactions([]); // Clear local UI
      fetchStats(); // Update stats to zero
      toast.success("Memória do servidor limpa!");
    } catch (e) {
      toast.error("Erro ao limpar dados no servidor");
    }
  }, [fetchStats]);

  return {
    transactions, // Still used by your UI list
    statistics,   // Now strictly from Java
    addTransaction,
    clearTransactions,
    totalCount: transactions.length,
  };
}