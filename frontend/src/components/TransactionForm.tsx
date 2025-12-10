import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Plus, Calendar, DollarSign } from 'lucide-react';
import { toast } from 'sonner';

interface TransactionFormProps {
  onSubmit: (valor: number, dataHora: string) => { success: boolean; error?: string };
}

export function TransactionForm({ onSubmit }: TransactionFormProps) {
  const [valor, setValor] = useState('');
  const [dataHora, setDataHora] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const valorNumber = parseFloat(valor);
    const result = onSubmit(valorNumber, dataHora);

    if (result.success) {
      toast.success('Transação registrada com sucesso!');
      setValor('');
      setDataHora('');
    } else {
      toast.error(result.error || 'Erro ao registrar transação');
    }
  };

  const setCurrentDateTime = () => {
    const now = new Date();
    const offset = now.getTimezoneOffset();
    const localDate = new Date(now.getTime() - offset * 60000);
    setDataHora(localDate.toISOString().slice(0, 16));
  };

  return (
    <Card className="shadow-card border-border/50 animate-slide-up">
      <CardHeader className="pb-4">
        <CardTitle className="text-lg font-semibold flex items-center gap-2">
          <div className="h-8 w-8 rounded-lg gradient-accent flex items-center justify-center">
            <Plus className="h-4 w-4 text-accent-foreground" />
          </div>
          Nova Transação
        </CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="valor" className="text-sm font-medium">
              Valor (R$)
            </Label>
            <div className="relative">
              <DollarSign className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                id="valor"
                type="number"
                step="0.01"
                min="0"
                placeholder="0.00"
                value={valor}
                onChange={(e) => setValor(e.target.value)}
                className="pl-10 h-11"
                required
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="dataHora" className="text-sm font-medium">
              Data e Hora
            </Label>
            <div className="relative">
              <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                id="dataHora"
                type="datetime-local"
                value={dataHora}
                onChange={(e) => setDataHora(e.target.value)}
                className="pl-10 h-11"
                required
              />
            </div>
            <Button
              type="button"
              variant="ghost"
              size="sm"
              onClick={setCurrentDateTime}
              className="text-xs text-accent hover:text-accent/80"
            >
              Usar data/hora atual
            </Button>
          </div>

          <Button type="submit" variant="accent" className="w-full h-11">
            <Plus className="h-4 w-4" />
            Registrar Transação
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}
