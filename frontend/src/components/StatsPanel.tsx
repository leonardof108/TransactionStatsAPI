import { Statistics } from '@/types/transaction';
import { StatCard } from './StatCard';
import { Hash, TrendingUp, Calculator, ArrowDown, ArrowUp } from 'lucide-react';

interface StatsPanelProps {
  statistics: Statistics;
}

export function StatsPanel({ statistics }: StatsPanelProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2 mb-2">
        <h2 className="text-lg font-semibold">Estatísticas</h2>
        <span className="text-xs font-medium text-muted-foreground bg-secondary px-2 py-0.5 rounded-full">
          Últimos 60 segundos
        </span>
      </div>
      
      <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
        <StatCard
          label="Quantidade"
          value={statistics.count}
          format="number"
          icon={<Hash className="h-5 w-5 text-muted-foreground" />}
          highlight
        />
        <StatCard
          label="Soma Total"
          value={statistics.sum}
          format="currency"
          icon={<TrendingUp className="h-5 w-5 text-muted-foreground" />}
        />
        <StatCard
          label="Média"
          value={statistics.avg}
          format="currency"
          icon={<Calculator className="h-5 w-5 text-muted-foreground" />}
        />
        <StatCard
          label="Mínimo"
          value={statistics.min}
          format="currency"
          icon={<ArrowDown className="h-5 w-5 text-muted-foreground" />}
        />
        <StatCard
          label="Máximo"
          value={statistics.max}
          format="currency"
          icon={<ArrowUp className="h-5 w-5 text-muted-foreground" />}
        />
      </div>
    </div>
  );
}
