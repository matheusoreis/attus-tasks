import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { TaskList } from "./components/TaskList";

import "./index.css";
import { ThemeProvider } from "./components/theme/Provider";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ThemeProvider>
      <TaskList />
    </ThemeProvider>
  </StrictMode>,
);
