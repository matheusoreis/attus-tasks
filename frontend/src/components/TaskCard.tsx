import { type ReactNode } from "react";
import { type Task, type TaskStatus } from "@/services/taskService";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Trash2,
  CircleDashed,
  CircleEllipsis,
  CircleCheck,
} from "lucide-react";

interface TaskCardProps {
  task: Task;
  onStatusChange: (id: number, status: TaskStatus) => void;
  onEdit: (task: Task) => void;
  onDelete: (id: number) => void;
}

const statusConfig: Record<
  TaskStatus,
  {
    label: string;
    next: TaskStatus | null;
    nextLabel: string | null;
    icon: ReactNode;
    badge: string;
  }
> = {
  TODO: {
    label: "Todo",
    next: "IN_PROGRESS",
    nextLabel: "Iniciar",
    icon: <CircleDashed className="w-5 h-5 text-muted-foreground" />,
    badge: "secondary",
  },
  IN_PROGRESS: {
    label: "In progress",
    next: "DONE",
    nextLabel: "Concluir",
    icon: <CircleEllipsis className="w-5 h-5 text-blue-500" />,
    badge: "blue",
  },
  DONE: {
    label: "Done",
    next: "TODO",
    nextLabel: "Reabrir",
    icon: <CircleCheck className="w-5 h-5 text-green-600" />,
    badge: "green",
  },
};

const badgeStyles: Record<string, string> = {
  secondary: "bg-muted text-muted-foreground",
  blue: "bg-blue-50 text-blue-700 border-blue-200",
  green: "bg-green-50 text-green-700 border-green-200",
};

export function TaskCard({
  task,
  onStatusChange,
  onEdit,
  onDelete,
}: TaskCardProps) {
  const config = statusConfig[task.status];
  const isDone = task.status === "DONE";

  return (
    <Card className={isDone ? "opacity-60" : ""}>
      <CardContent className="flex items-center justify-between gap-4 py-4">
        <div className="flex items-center gap-3 flex-1 min-w-0">
          {config.icon}
          <div className="min-w-0">
            <p
              className={`font-medium text-sm truncate ${
                isDone ? "line-through text-muted-foreground" : ""
              }`}
            >
              {task.title}
            </p>
            {task.description && (
              <p className="text-xs text-muted-foreground truncate mt-0.5">
                {task.description}
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <Badge
            variant="outline"
            className={`text-xs rounded-full ${badgeStyles[config.badge]}`}
          >
            {config.label}
          </Badge>

          {config.next && (
            <Button
              size="sm"
              variant="outline"
              onClick={() => onStatusChange(task.id, config.next!)}
            >
              {config.nextLabel}
            </Button>
          )}

          <Button size="sm" variant="ghost" onClick={() => onEdit(task)}>
            Editar
          </Button>

          <Button size="sm" variant="ghost" onClick={() => onDelete(task.id)}>
            <Trash2 className="w-4 h-4 text-muted-foreground" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
