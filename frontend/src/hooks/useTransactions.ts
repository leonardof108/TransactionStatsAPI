import { useState, useCallback, useEffect } from 'react';
import { Transaction, Statistics } from '@/types/transaction';
import { toast } from "sonner";

export function useTransactions() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  const [statistics, setStatistics] = useState<Statistics>({
    count: 0, sum: 0, avg: 0, min: 0, max: 0
  });

  const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

  // Helper: Checks which transactions are younger than 60s
  const updateRecency = useCallback(() => {
    const now = new Date().getTime();
    setTransactions(prev => prev.map(t => {
      const txTime = new Date(t.dataHora).getTime();
      const diffSeconds = (now - txTime) / 1000;
      // It is recent if less than 60s passed
      const isRecent = diffSeconds < 60;

      // Only update if value changed (performance optimization)
      return t.isRecent === isRecent ? t : { ...t, isRecent };
    }));
  }, []);

  // 1. POLL STATISTICS & UPDATE UI COLORS
  const fetchStats = useCallback(async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/estatistica`);
      if (response.ok) {
        const data = await response.json();
        setStatistics(data);
      }
    } catch (error) {
      console.error("Failed to fetch stats from Java:", error);
    }
  }, []);

  useEffect(() => {
    fetchStats();
    updateRecency();

    const interval = setInterval(() => {
      fetchStats();    // Get new numbers from Java
      updateRecency(); // Update Orange/Grey colors in UI
    }, 1000);

    return () => clearInterval(interval);
  }, [fetchStats, updateRecency]);

  // 2. ADD TRANSACTION
  const addTransaction = useCallback(async (valor: number, dataHora: string) => {
    try {
      const response = await fetch(`${API_BASE_URL}/transacao`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ valor, dataHora })
      });

      if (response.status === 201) {
        const newTransaction: Transaction = {
            id: crypto.randomUUID(),
            valor,
            dataHora,
            isRecent: true, // <--- Starts as Orange!
        };
        setTransactions(prev => [newTransaction, ...prev]);

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

  // 3. CLEAR TRANSACTIONS
  const clearTransactions = useCallback(async () => {
    try {
      await fetch(`${API_BASE_URL}/transacao`, { method: 'DELETE' });
      setTransactions([]);
      fetchStats();
      toast.success("Memória do servidor limpa!");
    } catch (e) {
      toast.error("Erro ao limpar dados no servidor");
    }
  }, [fetchStats]);

  return {
    transactions,
    statistics,
    addTransaction,
    clearTransactions,
    totalCount: transactions.length,
  };
}