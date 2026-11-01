ALTER TABLE alias_map
    ADD COLUMN case_sensitive BOOLEAN NOT NULL DEFAULT FALSE;

-- case_sensitive = TRUE for names that are also common English words ("go", "rust", "react").
INSERT INTO alias_map (alias, canonical_name, case_sensitive) VALUES
    -- =========================================================
    -- Languages
    -- =========================================================
    ('Go',              'Go',              TRUE),
    ('Golang',          'Go',              FALSE),
    ('Rust',            'Rust',            TRUE),
    ('Python',          'Python',          FALSE),
    ('Java',            'Java',            TRUE),
    ('JavaScript',      'JavaScript',      FALSE),
    ('JS',              'JavaScript',      FALSE),
    ('TypeScript',      'TypeScript',      FALSE),
    ('Kotlin',          'Kotlin',          FALSE),
    ('Swift',           'Swift',           TRUE),
    ('Ruby',            'Ruby',            TRUE),
    ('PHP',             'PHP',             FALSE),
    ('C++',             'C++',             FALSE),
    ('C#',              'C#',              FALSE),
    ('Dart',             'Dart',            TRUE),
    ('Scala',            'Scala',            FALSE),
    ('Elixir',           'Elixir',           FALSE),
    ('Zig',              'Zig',              FALSE),
    ('Haskell',          'Haskell',          FALSE),
    ('Clojure',          'Clojure',          FALSE),
    ('Objective-C',      'Objective-C',      FALSE),
    ('Perl',             'Perl',             FALSE),
    ('Lua',              'Lua',              FALSE),
    ('Groovy',           'Groovy',           TRUE),
    ('MATLAB',           'MATLAB',           FALSE),
    ('Solidity',         'Solidity',         FALSE),
    ('COBOL',             'COBOL',             FALSE),
    ('Fortran',           'Fortran',           FALSE),

    -- =========================================================
    -- Frontend / Web Frameworks
    -- =========================================================
    ('React',             'React',             TRUE),
    ('ReactJS',           'React',             FALSE),
    ('React.js',          'React',             FALSE),
    ('Vue',               'Vue',               FALSE),
    ('Vue.js',             'Vue',               FALSE),
    ('VueJS',              'Vue',               FALSE),
    ('Angular',            'Angular',           FALSE),
    ('Svelte',             'Svelte',            FALSE),
    ('SvelteKit',          'SvelteKit',         FALSE),
    ('Next.js',            'Next.js',           FALSE),
    ('NextJS',             'Next.js',           FALSE),
    ('Nuxt',               'Nuxt',              FALSE),
    ('Nuxt.js',             'Nuxt',              FALSE),
    ('Remix',              'Remix',             TRUE),
    ('Astro',              'Astro',              TRUE),
    ('Gatsby',             'Gatsby',            TRUE),
    ('SolidJS',             'SolidJS',           FALSE),
    ('Solid.js',            'SolidJS',           FALSE),
    ('Qwik',               'Qwik',              FALSE),
    ('Ember.js',            'Ember.js',           FALSE),
    ('Alpine.js',           'Alpine.js',          FALSE),
    ('Lit',                 'Lit',               TRUE),
    ('jQuery',              'jQuery',            FALSE),
    ('Three.js',            'Three.js',          FALSE),

    -- CSS / UI
    ('Tailwind CSS',        'Tailwind CSS',      FALSE),
    ('Tailwind',            'Tailwind CSS',      FALSE),
    ('Bootstrap',           'Bootstrap',         FALSE),
    ('Material UI',         'Material UI',       FALSE),
    ('MUI',                 'Material UI',       FALSE),
    ('Chakra UI',           'Chakra UI',         FALSE),
    ('Ant Design',          'Ant Design',        FALSE),
    ('shadcn',              'shadcn/ui',         FALSE),
    ('shadcn/ui',           'shadcn/ui',         FALSE),

    -- Frontend tooling
    ('Webpack',             'Webpack',            FALSE),
    ('Vite',                'Vite',               FALSE),
    ('Parcel',              'Parcel',             TRUE),
    ('Rollup',              'Rollup',             TRUE),
    ('Babel',               'Babel',              FALSE),
    ('ESLint',              'ESLint',             FALSE),
    ('Prettier',             'Prettier',           TRUE),
    ('Storybook',            'Storybook',          FALSE),

    -- =========================================================
    -- Backend / Runtime / Frameworks
    -- =========================================================
    ('Spring',              'Spring',             TRUE),
    ('Spring Boot',         'Spring Boot',        FALSE),
    ('Spring MVC',          'Spring MVC',         FALSE),
    ('Spring Security',     'Spring Security',    FALSE),
    ('Django',              'Django',             FALSE),
    ('Flask',               'Flask',              TRUE),
    ('FastAPI',              'FastAPI',             FALSE),
    ('Express.js',          'Express.js',         FALSE),
    ('Express',             'Express.js',         TRUE),
    ('NestJS',              'NestJS',             FALSE),
    ('Nest.js',             'NestJS',             FALSE),
    ('Koa.js',              'Koa.js',             FALSE),
    ('Hono',                'Hono',               FALSE),
    ('.NET',                '.NET',               FALSE),
    ('ASP.NET',             'ASP.NET',            FALSE),
    ('ASP.NET Core',        'ASP.NET Core',       FALSE),
    ('Quarkus',             'Quarkus',            FALSE),
    ('Micronaut',           'Micronaut',          FALSE),
    ('Node.js',             'Node.js',            FALSE),
    ('NodeJS',              'Node.js',            FALSE),
    ('Deno',                'Deno',               FALSE),
    ('Bun',                 'Bun',                TRUE),
    ('Laravel',             'Laravel',            FALSE),
    ('Symfony',             'Symfony',            FALSE),
    ('CakePHP',             'CakePHP',            FALSE),
    ('Ruby on Rails',       'Ruby on Rails',      FALSE),
    ('Rails',               'Ruby on Rails',      TRUE),
    ('Gin',                 'Gin',                TRUE),
    ('Fiber',               'Fiber',              TRUE),
    ('Actix',               'Actix',              FALSE),
    ('Axum',                'Axum',               FALSE),

    -- =========================================================
    -- Databases / Data
    -- =========================================================
    ('MySQL',               'MySQL',              FALSE),
    ('PostgreSQL',          'PostgreSQL',         FALSE),
    ('Postgres',            'PostgreSQL',         FALSE),
    ('MongoDB',              'MongoDB',            FALSE),
    ('Redis',               'Redis',              FALSE),
    ('Cassandra',            'Cassandra',          FALSE),
    ('Elasticsearch',        'Elasticsearch',      FALSE),
    ('SQLite',               'SQLite',             FALSE),
    ('ClickHouse',           'ClickHouse',         FALSE),
    ('DuckDB',               'DuckDB',             FALSE),
    ('MariaDB',              'MariaDB',            FALSE),
    ('Oracle Database',      'Oracle Database',    FALSE),
    ('SQL Server',           'SQL Server',         FALSE),
    ('Microsoft SQL Server', 'SQL Server',         FALSE),
    ('MSSQL',                'SQL Server',         FALSE),
    ('CockroachDB',          'CockroachDB',        FALSE),
    ('Neo4j',                'Neo4j',              FALSE),
    ('DynamoDB',             'DynamoDB',            FALSE),
    ('Firestore',             'Firestore',           FALSE),
    ('Firebase',              'Firebase',            FALSE),
    ('Supabase',              'Supabase',            FALSE),
    ('PlanetScale',           'PlanetScale',         FALSE),

    -- =========================================================
    -- ORM / Data Access
    -- =========================================================
    ('Prisma',               'Prisma',             FALSE),
    ('Drizzle',              'Drizzle ORM',        FALSE),
    ('Drizzle ORM',          'Drizzle ORM',        FALSE),
    ('SQLAlchemy',           'SQLAlchemy',         FALSE),
    ('Mongoose',             'Mongoose',           FALSE),
    ('Sequelize',            'Sequelize',          FALSE),
    ('Hibernate',             'Hibernate',          FALSE),
    ('JPA',                  'JPA',                TRUE),

    -- =========================================================
    -- Messaging / Streaming
    -- =========================================================
    ('Kafka',                'Kafka',              FALSE),
    ('Apache Kafka',         'Kafka',              FALSE),
    ('RabbitMQ',              'RabbitMQ',           FALSE),
    ('ActiveMQ',              'ActiveMQ',           FALSE),
    ('NATS',                  'NATS',               FALSE),
    ('Apache Pulsar',         'Apache Pulsar',      FALSE),
    ('Pulsar',                'Apache Pulsar',      FALSE),
    ('Redpanda',              'Redpanda',           FALSE),
    ('Apache Flink',          'Apache Flink',       FALSE),
    ('Flink',                 'Apache Flink',       FALSE),
    ('Apache Spark',          'Apache Spark',       FALSE),
    ('Spark',                 'Apache Spark',       TRUE),
    ('Apache Airflow',        'Apache Airflow',     FALSE),
    ('Airflow',               'Apache Airflow',     TRUE),

    -- =========================================================
    -- AI / Machine Learning
    -- =========================================================
    ('OpenAI',                'OpenAI',             FALSE),
    ('ChatGPT',               'ChatGPT',            FALSE),
    ('GPT',                   'GPT',                TRUE),
    ('GPT-4',                 'GPT-4',              FALSE),
    ('GPT-4o',                'GPT-4o',             FALSE),
    ('GPT-5',                 'GPT-5',              FALSE),
    ('Claude',                'Claude',             FALSE),
    ('Anthropic',             'Anthropic',          FALSE),
    ('Gemini',                'Gemini',             FALSE),
    ('Google Gemini',         'Gemini',             FALSE),
    ('Llama',                 'Llama',              TRUE),
    ('Llama 2',               'Llama 2',            FALSE),
    ('Llama 3',               'Llama 3',            FALSE),
    ('Llama 4',               'Llama 4',            FALSE),
    ('Ollama',                'Ollama',             FALSE),
    ('Mistral',               'Mistral',            TRUE),
    ('Mixtral',               'Mixtral',            FALSE),
    ('DeepSeek',              'DeepSeek',           FALSE),
    ('Qwen',                  'Qwen',               FALSE),
    ('Gemma',                 'Gemma',              FALSE),
    ('Perplexity',            'Perplexity',         TRUE),
    ('Copilot',               'GitHub Copilot',     TRUE),
    ('GitHub Copilot',        'GitHub Copilot',     FALSE),
    ('Cursor',                'Cursor',             TRUE),
    ('PyTorch',               'PyTorch',            FALSE),
    ('TensorFlow',             'TensorFlow',         FALSE),
    ('Keras',                 'Keras',              FALSE),
    ('scikit-learn',          'scikit-learn',       FALSE),
    ('sklearn',               'scikit-learn',       FALSE),
    ('XGBoost',               'XGBoost',            FALSE),
    ('ONNX',                  'ONNX',               FALSE),
    ('CUDA',                  'CUDA',               FALSE),
    ('NVIDIA CUDA',           'CUDA',               FALSE),

    -- =========================================================
    -- LLM / AI Frameworks
    -- =========================================================
    ('LangChain',             'LangChain',          FALSE),
    ('LangGraph',             'LangGraph',          FALSE),
    ('LlamaIndex',            'LlamaIndex',         FALSE),
    ('Hugging Face',          'Hugging Face',       FALSE),
    ('HuggingFace',           'Hugging Face',       FALSE),
    ('Hugging Face Transformers',
                              'Hugging Face Transformers', FALSE),
    ('Semantic Kernel',       'Semantic Kernel',    FALSE),
    ('AutoGen',                'AutoGen',            FALSE),
    ('CrewAI',                'CrewAI',             FALSE),
    ('DSPy',                  'DSPy',               FALSE),
    ('vLLM',                  'vLLM',               FALSE),
    ('LM Studio',             'LM Studio',          FALSE),
    ('OpenRouter',             'OpenRouter',         FALSE),

    -- =========================================================
    -- Vector Database / RAG
    -- =========================================================
    ('FAISS',                 'FAISS',              FALSE),
    ('Chroma',                'Chroma',             TRUE),
    ('ChromaDB',              'Chroma',             FALSE),
    ('Pinecone',              'Pinecone',           FALSE),
    ('Weaviate',              'Weaviate',           FALSE),
    ('Milvus',                'Milvus',             FALSE),
    ('Qdrant',                'Qdrant',             FALSE),

    -- =========================================================
    -- Cloud
    -- =========================================================
    ('AWS',                   'AWS',                FALSE),
    ('Amazon Web Services',   'AWS',                FALSE),
    ('EC2',                   'Amazon EC2',         FALSE),
    ('Amazon EC2',            'Amazon EC2',         FALSE),
    ('S3',                    'Amazon S3',          FALSE),
    ('Amazon S3',             'Amazon S3',          FALSE),
    ('Lambda',                'AWS Lambda',         TRUE),
    ('AWS Lambda',            'AWS Lambda',         FALSE),
    ('ECS',                   'Amazon ECS',         FALSE),
    ('Amazon ECS',             'Amazon ECS',         FALSE),
    ('EKS',                   'Amazon EKS',         FALSE),
    ('Amazon EKS',             'Amazon EKS',         FALSE),
    ('RDS',                   'Amazon RDS',         FALSE),
    ('Amazon RDS',             'Amazon RDS',         FALSE),
    ('CloudFront',             'Amazon CloudFront',  FALSE),
    ('Amazon CloudFront',     'Amazon CloudFront',  FALSE),
    ('Azure',                 'Microsoft Azure',    FALSE),
    ('Microsoft Azure',       'Microsoft Azure',    FALSE),
    ('Azure OpenAI',           'Azure OpenAI',       FALSE),
    ('GCP',                   'Google Cloud',       FALSE),
    ('Google Cloud',          'Google Cloud',       FALSE),
    ('Google Cloud Platform', 'Google Cloud',       FALSE),
    ('Cloud Run',              'Google Cloud Run',   FALSE),
    ('Google Cloud Run',       'Google Cloud Run',   FALSE),
    ('Vercel',                'Vercel',             FALSE),
    ('Netlify',               'Netlify',            FALSE),
    ('Cloudflare',             'Cloudflare',         FALSE),
    ('Cloudflare Workers',    'Cloudflare Workers', FALSE),

    -- =========================================================
    -- DevOps / Infrastructure
    -- =========================================================
    ('Docker',                'Docker',             FALSE),
    ('Docker Compose',        'Docker Compose',     FALSE),
    ('Docker Swarm',          'Docker Swarm',       FALSE),
    ('Kubernetes',             'Kubernetes',         FALSE),
    ('K8s',                    'Kubernetes',         FALSE),
    ('OpenShift',              'OpenShift',          FALSE),
    ('Rancher',                'Rancher',            TRUE),
    ('containerd',             'containerd',         FALSE),
    ('CRI-O',                  'CRI-O',              FALSE),
    ('Podman',                 'Podman',             FALSE),
    ('Terraform',              'Terraform',          FALSE),
    ('Ansible',                'Ansible',            FALSE),
    ('Pulumi',                 'Pulumi',             FALSE),
    ('Packer',                 'Packer',             TRUE),
    ('Helm',                   'Helm',               TRUE),
    ('Helm Charts',            'Helm',               FALSE),
    ('Argo CD',                'Argo CD',            FALSE),
    ('ArgoCD',                 'Argo CD',            FALSE),
    ('Jenkins',                'Jenkins',            FALSE),
    ('GitLab CI',              'GitLab CI/CD',       FALSE),
    ('GitLab CI/CD',           'GitLab CI/CD',       FALSE),
    ('CircleCI',               'CircleCI',           FALSE),
    ('Travis CI',              'Travis CI',          FALSE),
    ('Istio',                  'Istio',              FALSE),
    ('Linkerd',                'Linkerd',            FALSE),
    ('Consul',                 'Consul',             TRUE),
    ('Vault',                  'HashiCorp Vault',    TRUE),
    ('HashiCorp Vault',        'HashiCorp Vault',    FALSE),
    ('Nomad',                  'HashiCorp Nomad',    TRUE),
    ('HashiCorp Nomad',        'HashiCorp Nomad',    FALSE),

    -- =========================================================
    -- Web Server / Proxy
    -- =========================================================
    ('Nginx',                  'Nginx',              FALSE),
    ('Apache HTTP Server',     'Apache HTTP Server', FALSE),
    ('Caddy',                  'Caddy',              FALSE),
    ('Traefik',                'Traefik',            FALSE),
    ('HAProxy',                'HAProxy',            FALSE),

    -- =========================================================
    -- Monitoring / Observability
    -- =========================================================
    ('Prometheus',             'Prometheus',         FALSE),
    ('Grafana',                'Grafana',            FALSE),
    ('OpenTelemetry',          'OpenTelemetry',      FALSE),
    ('OTel',                   'OpenTelemetry',      FALSE),
    ('Jaeger',                 'Jaeger',             FALSE),
    ('Loki',                   'Grafana Loki',       TRUE),
    ('Grafana Loki',           'Grafana Loki',       FALSE),
    ('Tempo',                  'Grafana Tempo',      TRUE),
    ('Grafana Tempo',          'Grafana Tempo',      FALSE),
    ('Sentry',                 'Sentry',             TRUE),
    ('Datadog',                'Datadog',            FALSE),
    ('New Relic',              'New Relic',          FALSE),
    ('ELK',                    'ELK Stack',          FALSE),
    ('ELK Stack',              'ELK Stack',          FALSE),
    ('Logstash',               'Logstash',           FALSE),
    ('Kibana',                 'Kibana',             FALSE),

    -- =========================================================
    -- Version Control / Developer Tools
    -- =========================================================
    ('Git',                    'Git',                FALSE),
    ('GitHub',                 'GitHub',             FALSE),
    ('GitLab',                 'GitLab',             FALSE),
    ('Bitbucket',              'Bitbucket',          FALSE),
    ('GitHub CLI',             'GitHub CLI',         FALSE),
    ('gh',                     'GitHub CLI',         TRUE),
    ('npm',                    'npm',                FALSE),
    ('pnpm',                   'pnpm',               TRUE),
    ('Yarn',                   'Yarn',               FALSE),

    -- =========================================================
    -- Mobile
    -- =========================================================
    ('Android',                'Android',            FALSE),
    ('Android SDK',            'Android SDK',        FALSE),
    ('iOS',                    'iOS',                TRUE),
    ('SwiftUI',                'SwiftUI',            FALSE),
    ('Jetpack Compose',        'Jetpack Compose',    FALSE),
    ('Flutter',                'Flutter',            FALSE),
    ('React Native',           'React Native',       FALSE),
    ('Expo',                   'Expo',               TRUE),
    ('Ionic',                  'Ionic',              TRUE),
    ('Capacitor',              'Capacitor',          TRUE),

    -- =========================================================
    -- Testing
    -- =========================================================
    ('JUnit',                  'JUnit',              FALSE),
    ('JUnit 4',                'JUnit 4',            FALSE),
    ('JUnit 5',                'JUnit 5',            FALSE),
    ('TestNG',                 'TestNG',             FALSE),
    ('Mockito',                'Mockito',            FALSE),
    ('Jest',                   'Jest',               TRUE),
    ('Vitest',                 'Vitest',             FALSE),
    ('Mocha',                  'Mocha',              TRUE),
    ('Cypress',                'Cypress',            FALSE),
    ('Playwright',              'Playwright',         FALSE),
    ('Selenium',               'Selenium',           FALSE),
    ('Appium',                 'Appium',             FALSE),
    ('k6',                     'k6',                 TRUE),
    ('Postman',                'Postman',            TRUE),
    ('Insomnia',               'Insomnia',           TRUE),

    -- =========================================================
    -- Security / Authentication
    -- =========================================================
    ('OAuth',                  'OAuth',              FALSE),
    ('OAuth 2.0',              'OAuth 2.0',          FALSE),
    ('OpenID Connect',          'OpenID Connect',     FALSE),
    ('OIDC',                   'OpenID Connect',     FALSE),
    ('JWT',                    'JWT',                TRUE),
    ('Keycloak',               'Keycloak',           FALSE),
    ('Auth0',                  'Auth0',              FALSE),
    ('Okta',                   'Okta',               FALSE),
    ('OWASP',                  'OWASP',              FALSE),
    ('WebAuthn',               'WebAuthn',           FALSE),
    ('Passkeys',               'Passkeys',           FALSE),

    -- =========================================================
    -- API / Protocols
    -- =========================================================
    ('REST API',               'REST API',           FALSE),
    ('RESTful API',            'REST API',           FALSE),
    ('GraphQL',                'GraphQL',            FALSE),
    ('gRPC',                   'gRPC',               FALSE),
    ('WebSocket',              'WebSocket',          FALSE),
    ('WebSockets',             'WebSocket',          FALSE),
    ('WebRTC',                 'WebRTC',             FALSE),
    ('SOAP',                   'SOAP',              FALSE),
    ('OpenAPI',                'OpenAPI',            FALSE),
    ('Swagger',                'OpenAPI',            FALSE),
    ('AsyncAPI',               'AsyncAPI',           FALSE),

    -- =========================================================
    -- Blockchain / Web3
    -- =========================================================
    ('Ethereum',               'Ethereum',           FALSE),
    ('Solana',                 'Solana',             FALSE),
    ('Web3',                   'Web3',               FALSE),
    ('EVM',                    'EVM',                TRUE),
    ('Hardhat',                'Hardhat',            TRUE),
    ('Foundry',                'Foundry',            TRUE),

    -- =========================================================
    -- Game Development
    -- =========================================================
    ('Unreal Engine',          'Unreal Engine',      FALSE),
    ('Unreal',                 'Unreal Engine',      FALSE),
    ('Godot',                  'Godot',              FALSE),
    ('GameMaker',              'GameMaker',          FALSE);