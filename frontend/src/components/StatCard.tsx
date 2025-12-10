import { cn } from '@/lib/utils';

interface StatCardProps {
  label: string;
  value: number;
  format?: 'currency' | 'number' | 'decimal';
  icon: React.ReactNode;
  highlight?: boolean;
}

export function StatCard({ label, value, format = 'number', icon, highlight }: StatCardProps) {
  const formatValue = () => {
    switch (format) {
      case 'currency':
        return new Intl.NumberFormat('pt-BR', {
          style: 'currency',
          currency: 'BRL',
        }).format(value);
      case 'decimal':
        return new Intl.NumberFormat('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        }).format(value);
      default:
        return value.toString();
    }
  };

  return (
    <div
      className={cn(
        "relative overflow-hidden rounded-xl p-5 transition-all duration-300 hover:scale-[1.02]",
        highlight
          ? "gradient-accent text-accent-foreground shadow-accent"
          : "bg-card shadow-card border border-border/50"
      )}
    >
      <div className="flex items-start justify-between">
        <div className="space-y-2">
          <p className={cn(
            "text-sm font-medium",
            highlight ? "text-accent-foreground/80" : "text-muted-foreground"
          )}>
            {label}
          </p>
          <p className="text-2xl font-bold tracking-tight animate-fade-in">
            {formatValue()}
          </p>
        </div>
        <div className={cn(
          "rounded-lg p-2.5",
          highlight ? "bg-accent-foreground/10" : "bg-secondary"
        )}>
          {icon}
        </div>
      </div>
      {highlight && (
        <div className="absolute -bottom-10 -right-10 h-32 w-32 rounded-full bg-accent-foreground/5" />
      )}
    </div>
  );
}
