import { useState, useActionState } from "react";
import { useFormStatus } from "react-dom";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { type Task, type TaskRequest } from "@/services/taskService";

interface TaskFormProps {
  open: boolean;
  task?: Task | null;
  onClose: () => void;
  onSubmit: (data: TaskRequest) => Promise<void>;
}

function SubmitButton({ isEdit }: { isEdit: boolean }) {
  const { pending } = useFormStatus();
  return (
    <Button type="submit" disabled={pending}>
      {pending ? "Salvando…" : isEdit ? "Salvar" : "Criar"}
    </Button>
  );
}

interface TaskFormFieldsProps {
  task?: Task | null;
  onClose: () => void;
  onSubmit: (data: TaskRequest) => Promise<void>;
}

type FormState = { error?: string };

function TaskFormFields({ task, onClose, onSubmit }: TaskFormFieldsProps) {
  const [title, setTitle] = useState(task?.title ?? "");
  const [description, setDescription] = useState(task?.description ?? "");

  const [state, formAction, isPending] = useActionState(
    async (_prev: FormState, formData: FormData): Promise<FormState> => {
      const t = (formData.get("title") as string) ?? "";
      const d = (formData.get("description") as string) ?? "";

      if (!t.trim()) return { error: "Título é obrigatório" };
      if (t.length > 100) return { error: "Máximo 100 caracteres" };

      await onSubmit({ title: t.trim(), description: d.trim() });
      return {};
    },
    {},
  );

  return (
    <form action={formAction} className="flex flex-col gap-4 py-2">
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="title">Título *</Label>
        <Input
          id="title"
          name="title"
          placeholder="Ex: Implementar autenticação"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          maxLength={100}
        />
        {state.error && <p className="text-xs text-red-500">{state.error}</p>}
        <p className="text-xs text-muted-foreground text-right">
          {title.length}/100
        </p>
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="description">Descrição</Label>
        <Textarea
          id="description"
          name="description"
          placeholder="Descreva a task…"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          maxLength={500}
          rows={3}
        />
        <p className="text-xs text-muted-foreground text-right">
          {description.length}/500
        </p>
      </div>

      <DialogFooter>
        <Button
          type="button"
          variant="outline"
          onClick={onClose}
          disabled={isPending}
        >
          Cancelar
        </Button>
        <SubmitButton isEdit={!!task} />
      </DialogFooter>
    </form>
  );
}

export function TaskForm({ open, task, onClose, onSubmit }: TaskFormProps) {
  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{task ? "Editar task" : "Nova task"}</DialogTitle>
        </DialogHeader>

        <TaskFormFields
          key={task ? `edit-${task.id}` : `new-${String(open)}`}
          task={task}
          onClose={onClose}
          onSubmit={onSubmit}
        />
      </DialogContent>
    </Dialog>
  );
}
