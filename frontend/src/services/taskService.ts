import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  createdAt: string;
  updatedAt: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
}

export type TaskStatus = "TODO" | "IN_PROGRESS" | "DONE";

export const getTasks = async (status?: TaskStatus): Promise<Task[]> => {
  const params = status ? { status } : {};
  const response = await api.get<Task[]>("/tasks", { params });
  return response.data;
};

export const getTaskById = async (id: number): Promise<Task> => {
  const response = await api.get<Task>(`/tasks/${id}`);
  return response.data;
};

export const createTask = async (data: TaskRequest): Promise<Task> => {
  const response = await api.post<Task>("/tasks", data);
  return response.data;
};

export const updateTask = async (
  id: number,
  data: TaskRequest,
): Promise<Task> => {
  const response = await api.put<Task>(`/tasks/${id}`, data);
  return response.data;
};

export const updateTaskStatus = async (
  id: number,
  status: TaskStatus,
): Promise<Task> => {
  const response = await api.patch<Task>(`/tasks/${id}/status`, { status });
  return response.data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await api.delete(`/tasks/${id}`);
};
