import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { TaskList } from "./components/TaskList";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <TaskList />;
  </StrictMode>,
);
