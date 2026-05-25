import { type TaskStatus } from "@/services/taskService";
import { Button } from "@/components/ui/button";

interface StatusFilterProps {
  selected: TaskStatus | undefined;
  onChange: (status: TaskStatus | undefined) => void;
}

const filters: { label: string; value: TaskStatus | undefined }[] = [
  { label: "Todas", value: undefined },
  { label: "Todo", value: "TODO" },
  { label: "In progress", value: "IN_PROGRESS" },
  { label: "Done", value: "DONE" },
];

export function StatusFilter({ selected, onChange }: StatusFilterProps) {
  return (
    <div className="flex gap-2 flex-wrap">
      {filters.map((filter) => (
        <Button
          key={filter.label}
          variant={selected === filter.value ? "default" : "outline"}
          size="sm"
          className="rounded-full"
          onClick={() => onChange(filter.value)}
        >
          {filter.label}
        </Button>
      ))}
    </div>
  );
}
