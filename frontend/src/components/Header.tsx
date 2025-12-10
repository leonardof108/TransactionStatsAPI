import { Activity } from 'lucide-react';

export function Header() {
  return (
    <header className="border-b border-border/50 bg-card/80 backdrop-blur-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-xl gradient-accent flex items-center justify-center shadow-accent">
            <Activity className="h-5 w-5 text-accent-foreground" />
          </div>
          <div>
            <h1 className="text-xl font-bold tracking-tight">
              Transaction Statistics
            </h1>
            <p className="text-sm text-muted-foreground">
              Desafio Itaú - API REST
            </p>
          </div>
        </div>
      </div>
    </header>
  );
}
