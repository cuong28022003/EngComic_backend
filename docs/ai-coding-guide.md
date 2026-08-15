# AI CODING HANDBOOK & WORKFLOW GUIDE

> **Target Audience**: Developers pair-programming with AI on the `EngComic_backend` project  
> **Objective**: Step-by-step guidance on how to prompt, control, intervene, and guide AI during development to achieve maximum efficiency without context drift or architectural decay.

---

## 🧭 1. Overall Workflow Diagram

```mermaid
graph TD
    Start[Start Task] --> CheckType{Task Type?}
    
    %% Small task branch
    CheckType -- Small Bug Fix / Add 1 Field --> QuickPrompt[Single-sentence direct prompt<br>e.g. Fix NPE in ComicMapping.java]
    QuickPrompt --> QuickVerify[Run ./mvnw compile]
    
    %% New feature branch
    CheckType -- New Feature / Large Module --> Step0[Step 0: Copy specs/000-template to specs/00X-feature/]
    Step0 --> Step1[Step 1: Run Prompt to generate spec.md]
    Step1 --> Step2[Step 2: Run Prompt to generate plan.md]
    Step2 --> Step3[Step 3: Run Prompt to generate tasks.md]
    Step3 --> Step4[Step 4: Prompt AI to code tasks T001, T002... step by step]
    Step4 --> StepVerify{Is AI drifting off track?}
    StepVerify -- Yes --> Intervene[Intervene & stop AI]
    StepVerify -- No --> Done[Check checkbox [x] & proceed to next task]
```

---

## 2. Step-by-Step Instructions

### 📌 Case A: Developing a New Feature

#### Step 0: Create Spec Directory
Copy `specs/000-template/` to `specs/001-[feature-name]/` (e.g. `specs/001-daily-quest/`).

#### Step 1: Request `spec.md` (Business Specification)
> **Prompt:**
> ```text
> I want to implement a new feature: "[Feature Name]".
> Description: [Paste preliminary idea / requirements here].
> Read AGENTS.md and generate specs/001-[feature-name]/spec.md containing User Stories (P1, P2), BDD Acceptance Criteria (Given-When-Then), and Business Invariants.
> ```

#### Step 2: Request `plan.md` (Technical Architecture)
> **Prompt:**
> ```text
> Based on specs/001-[feature-name]/spec.md, design specs/001-[feature-name]/plan.md following Clean Architecture (apis, businesses, databases) and MongoDB schema design.
> ```

#### Step 3: Request `tasks.md` (Execution Checklist)
> **Prompt:**
> ```text
> Based on the newly created spec.md and plan.md, write specs/001-[feature-name]/tasks.md breaking down tasks into 4 Phases (databases -> businesses -> apis -> verification). Each task must specify Where, How, and Refs.
> ```

#### Step 4: Execute Tasks Sequentially
> **Standard Execution Prompt (Copy-paste):**
> ```text
> Read specs/001-[feature-name]/ and execute ONLY Task T001 in tasks.md.
> Upon completion:
> 1. Automatically check [x] in tasks.md.
> 2. Run './mvnw compile' to verify syntax.
> 3. STOP and report results. Do not proceed to the next task without prompt confirmation.
> ```

---

### 📌 Case B: Small Bug Fixes & Code Tweaks

No spec creation required. Provide a clear prompt covering 3 key elements: **Affected File + Error Symptom + Fix Target**.

> **Sample Prompt:**
> ```text
> File 'src/main/java/mobile/mapping/ComicMapping.java' throws a NullPointerException when 'comic.getUploaderId()' is null.
> Add null-safe checks before calling userService.findById(). Then run './mvnw compile' to verify.
> ```

---

## 🛑 3. When to STOP and INTERVENE with AI

| Anomalous Symptom | Cause | Action / Intervention Prompt |
| :--- | :--- | :--- |
| **AI modifies unrelated existing files** | Context drift, trying to refactor entire project | 🛑 **STOP IMMEDIATELY**: *"Revert all changes outside scope. You are ONLY allowed to edit files within Task T00X."* |
| **AI writes business logic inside Controller** | Violates Thin Controller principle | 🛑 **REMIND**: *"AGENTS.md Violation: Controllers must not contain business logic. Move all calculation logic to the Interactor in `businesses/`."* |
| **AI generates verbose Lombok classes instead of Java 21 Records** | Outdated coding pattern | 🛑 **SPECIFY**: *"Use Java 21 `record` syntax for this Boundary Request/Response instead of standard classes."* |
| **AI gets stuck in a compile error loop 3 times** | AI trapped in a fix loop | 🛑 **STOP**: *"Stop. Carefully read line X of the compile output, verify import statements, and do not guess blindly."* |

---

## 💡 4. The 5 Golden Prompting Rules

1. **Rule of "1 Prompt = 1 Small Task"**: Never tell AI *"Write the entire module X for me"*.
2. **Rule of "Always Compile Checkpoint"**: Always require AI to run `./mvnw compile` after every task.
3. **Rule of "Explicit File Pathing"**: Provide exact file paths (`Where: src/main/java/mobile/...`) so AI creates files in expected locations.
4. **Rule of "No Swallowing Errors"**: Prohibit empty `try { ... } catch (Exception e) {}` blocks.
5. **Rule of "Preserve Legacy Code Integrity"**: Remind AI never to refactor working, stable codebase areas.
