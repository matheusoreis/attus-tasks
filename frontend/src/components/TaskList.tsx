import { use, useOptimistic, useState, useTransition, Suspense } from "react";
import {
  getTasks,
  createTask,
  updateTask,
  updateTaskStatus,
  deleteTask,
} from "../services/taskService";
import type { Task, TaskStatus, TaskRequest } from "../services/taskService";
import { TaskCard } from "./TaskCard";
import { TaskForm } from "./TaskForm";
import { StatusFilter } from "./StatusFilter";
import { Button } from "./ui/button";
import { Plus } from "lucide-react";
import { ThemeToggle } from "./theme/Toggle";

type OptimisticAction =
  | { type: "status_change"; id: number; status: TaskStatus }
  | { type: "delete"; id: number };

function applyOptimistic(tasks: Task[], action: OptimisticAction): Task[] {
  switch (action.type) {
    case "status_change":
      return tasks.map((t) =>
        t.id === action.id ? { ...t, status: action.status } : t,
      );
    case "delete":
      return tasks.filter((t) => t.id !== action.id);
  }
}

function createTasksPromise(status?: TaskStatus) {
  return getTasks(status);
}

function TasksContent({
  tasksPromise,
  currentStatus,
  setTasksPromise,
  onEdit,
}: {
  tasksPromise: Promise<Task[]>;
  currentStatus: TaskStatus | undefined;
  setTasksPromise: (p: Promise<Task[]>) => void;
  onEdit: (task: Task) => void;
}) {
  const tasks = use(tasksPromise);

  const [optimisticTasks, addOptimistic] = useOptimistic(
    tasks,
    applyOptimistic,
  );

  const [isPending, startTransition] = useTransition();

  const refresh = () => setTasksPromise(createTasksPromise(currentStatus));

  const handleStatusChange = (id: number, newStatus: TaskStatus) => {
    startTransition(async () => {
      addOptimistic({ type: "status_change", id, status: newStatus });
      await updateTaskStatus(id, newStatus);
      refresh();
    });
  };

  const handleDelete = (id: number) => {
    startTransition(async () => {
      addOptimistic({ type: "delete", id });
      await deleteTask(id);
      refresh();
    });
  };

  const counts = {
    TODO: optimisticTasks.filter((t) => {
      return t.status === "TODO";
    }).length,

    IN_PROGRESS: optimisticTasks.filter((t) => {
      return t.status === "IN_PROGRESS";
    }).length,

    DONE: optimisticTasks.filter((t) => {
      return t.status === "DONE";
    }).length,
  };

  return (
    <>
      <div className="grid grid-cols-3 gap-3">
        {[
          { label: "Todo", value: counts.TODO },
          { label: "In progress", value: counts.IN_PROGRESS },
          { label: "Done", value: counts.DONE },
        ].map((m) => (
          <div key={m.label} className="bg-muted rounded-lg p-4 text-center">
            <p className="text-xs text-muted-foreground mb-1">{m.label}</p>
            <p className="text-2xl font-medium">{m.value}</p>
          </div>
        ))}
      </div>

      {optimisticTasks.length === 0 ? (
        <p className="text-sm text-muted-foreground text-center py-8">
          Nenhuma task encontrada.
        </p>
      ) : (
        <div
          className={`flex flex-col gap-3 transition-opacity pt-8 ${isPending ? "opacity-60" : ""}`}
        >
          {optimisticTasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              onStatusChange={handleStatusChange}
              onEdit={onEdit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </>
  );
}

export function TaskList() {
  const [status, setStatus] = useState<TaskStatus | undefined>(undefined);
  const [tasksPromise, setTasksPromise] = useState(() => createTasksPromise());
  const [formOpen, setFormOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [isFilterPending, startFilterTransition] = useTransition();

  const handleFilterChange = (s: TaskStatus | undefined) => {
    setStatus(s);
    startFilterTransition(() => {
      setTasksPromise(createTasksPromise(s));
    });
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setFormOpen(true);
  };

  const handleFormSubmit = async (data: TaskRequest) => {
    if (editingTask) {
      await updateTask(editingTask.id, data);
    } else {
      await createTask(data);
    }
    setFormOpen(false);
    setEditingTask(null);
    startFilterTransition(() => {
      setTasksPromise(createTasksPromise(status));
    });
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8 flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-medium">Task Manager</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Gerencie suas tarefas
          </p>
        </div>
        <div className="flex items-center gap-2">
          <ThemeToggle />
          <Button onClick={() => setFormOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Nova task
          </Button>
        </div>
      </div>

      <StatusFilter selected={status} onChange={handleFilterChange} />

      <div
        className={`transition-opacity ${isFilterPending ? "opacity-50" : ""}`}
      >
        <Suspense
          fallback={
            <p className="text-sm text-muted-foreground text-center py-8">
              Carregando…
            </p>
          }
        >
          <TasksContent
            tasksPromise={tasksPromise}
            currentStatus={status}
            setTasksPromise={setTasksPromise}
            onEdit={handleEdit}
          />
        </Suspense>
      </div>

      <TaskForm
        open={formOpen}
        task={editingTask}
        onClose={() => {
          setFormOpen(false);
          setEditingTask(null);
        }}
        onSubmit={handleFormSubmit}
      />
    </div>
  );
}
