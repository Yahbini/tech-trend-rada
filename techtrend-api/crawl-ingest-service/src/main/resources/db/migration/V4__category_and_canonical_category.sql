INSERT INTO category (name, code, description) VALUES
    ('Programming language', 'language',  'General-purpose and domain-specific languages'),
    ('Database',             'database',  'Relational, NoSQL, vector databases and search engines'),
    ('Framework / library',  'framework', 'Web, mobile, UI, ORM, runtime and game frameworks'),
    ('AI tool',              'ai-tool',   'LLMs, AI products, ML frameworks and LLM tooling'),
    ('DevOps',               'devops',    'Containers, orchestration, CI/CD, IaC, observability, proxies'),
    ('Cloud',                'cloud',     'Cloud providers, managed services, hosting and BaaS'),
    ('Messaging',            'messaging', 'Message brokers and event streaming'),
    ('Testing',              'testing',   'Test frameworks, E2E and load testing tools'),
    ('Developer tool',       'tool',      'VCS, package managers, bundlers, linters, API clients'),
    ('Other',                'other',     'Protocols, standards, platforms and anything unmapped');

-- One category per canonical name; canonicals not listed here fall back to 'other' in code.
CREATE TABLE canonical_category (
    canonical_name VARCHAR(255) PRIMARY KEY,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_cc_category FOREIGN KEY (category_id) REFERENCES category(id)
);

-- language
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "C#", "C++", "COBOL", "Clojure", "Dart", "Elixir", "Fortran", "Go",
  "Groovy", "Haskell", "Java", "JavaScript", "Kotlin", "Lua", "MATLAB", "Objective-C",
  "PHP", "Perl", "Python", "Ruby", "Rust", "Scala", "Solidity", "Swift",
  "TypeScript", "Zig"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'language';

-- database
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "Cassandra", "Chroma", "ClickHouse", "CockroachDB", "DuckDB", "DynamoDB", "Elasticsearch", "FAISS",
  "Firestore", "MariaDB", "Milvus", "MongoDB", "MySQL", "Neo4j", "Oracle Database", "Pinecone",
  "PlanetScale", "PostgreSQL", "Qdrant", "Redis", "SQL Server", "SQLite", "Weaviate"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'database';

-- framework
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  ".NET", "ASP.NET", "ASP.NET Core", "Actix", "Alpine.js", "Android SDK", "Angular", "Ant Design",
  "Apache Flink", "Apache Spark", "Astro", "Axum", "Bootstrap", "Bun", "CakePHP", "Capacitor",
  "Chakra UI", "Deno", "Django", "Drizzle ORM", "Ember.js", "Expo", "Express.js", "FastAPI",
  "Fiber", "Flask", "Flutter", "GameMaker", "Gatsby", "Gin", "Godot", "Hibernate",
  "Hono", "Ionic", "JPA", "Jetpack Compose", "Koa.js", "Laravel", "Lit", "Material UI",
  "Micronaut", "Mongoose", "NestJS", "Next.js", "Node.js", "Nuxt", "Prisma", "Quarkus",
  "Qwik", "React", "React Native", "Remix", "Ruby on Rails", "SQLAlchemy", "Sequelize", "SolidJS",
  "Spring", "Spring Boot", "Spring MVC", "Spring Security", "Svelte", "SvelteKit", "SwiftUI", "Symfony",
  "Tailwind CSS", "Three.js", "Unreal Engine", "Vue", "jQuery", "shadcn/ui"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'framework';

-- ai-tool
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "Anthropic", "AutoGen", "Azure OpenAI", "CUDA", "ChatGPT", "Claude", "CrewAI", "Cursor",
  "DSPy", "DeepSeek", "GPT", "GPT-4", "GPT-4o", "GPT-5", "Gemini", "Gemma",
  "GitHub Copilot", "Hugging Face", "Keras", "LM Studio", "LangChain", "LangGraph", "Llama", "Llama 2",
  "Llama 3", "Llama 4", "LlamaIndex", "Mistral", "Mixtral", "ONNX", "Ollama", "OpenAI",
  "OpenRouter", "Perplexity", "PyTorch", "Qwen", "Semantic Kernel", "TensorFlow", "XGBoost", "scikit-learn",
  "vLLM"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'ai-tool';


-- devops
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "Ansible", "Apache HTTP Server", "Argo CD", "CRI-O", "Caddy", "CircleCI", "Consul", "Datadog",
  "Docker", "Docker Compose", "Docker Swarm", "ELK Stack", "GitLab CI/CD", "Grafana", "Grafana Loki", "Grafana Tempo",
  "HAProxy", "HashiCorp Nomad", "HashiCorp Vault", "Helm", "Istio", "Jaeger", "Jenkins", "Kibana",
  "Kubernetes", "Linkerd", "Logstash", "New Relic", "Nginx", "OpenShift", "OpenTelemetry", "Packer",
  "Podman", "Prometheus", "Pulumi", "Rancher", "Sentry", "Terraform", "Traefik", "Travis CI",
  "containerd"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'devops';

-- cloud
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "AWS", "AWS Lambda", "Amazon CloudFront", "Amazon EC2", "Amazon ECS", "Amazon EKS", "Amazon RDS", "Amazon S3",
  "Cloudflare", "Cloudflare Workers", "Firebase", "Google Cloud", "Google Cloud Run", "Microsoft Azure", "Netlify", "Supabase",
  "Vercel"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'cloud';

-- messaging
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "ActiveMQ", "Apache Pulsar", "Kafka", "NATS", "RabbitMQ", "Redpanda"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'messaging';

-- testing
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "Appium", "Cypress", "JUnit", "JUnit 4", "JUnit 5", "Jest", "Mocha", "Mockito",
  "Playwright", "Selenium", "TestNG", "Vitest", "k6"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'testing';

-- tool
INSERT INTO canonical_category (canonical_name, category_id)
SELECT j.name, c.id
FROM category c
JOIN JSON_TABLE('[
  "Apache Airflow", "Babel", "Bitbucket", "ESLint", "Foundry", "Git", "GitHub", "GitHub CLI",
  "GitLab", "Hardhat", "Insomnia", "Parcel", "Postman", "Prettier", "Rollup", "Storybook",
  "Vite", "Webpack", "Yarn", "npm", "pnpm"
]', '$[*]' COLUMNS (name VARCHAR(255) PATH '$')) j
WHERE c.code = 'tool';