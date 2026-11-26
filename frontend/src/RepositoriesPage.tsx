
import { useEffect, useState } from 'react'
import {
  GitBranch,
  ExternalLink,
  RefreshCw,
  Search,
  GitFork,
  CheckCircle2,
  AlertCircle,
} from 'lucide-react'

type GitHubRepository = {
  name: string
  full_name: string
  html_url: string
  description: string | null
  fork: boolean
  default_branch: string
  updated_at: string
}

type SyncResult = {
  scanned: number
  imported: number
  updated: number
  skipped: number
  invalid: number
}

export default function RepositoriesPage() {
  const [repositories, setRepositories] = useState<GitHubRepository[]>([])
  const [loading, setLoading] = useState(true)
  const [syncing, setSyncing] = useState(false)
  const [error, setError] = useState('')
  const [syncError, setSyncError] = useState('')
  const [search, setSearch] = useState('')
  const [syncResult, setSyncResult] = useState<SyncResult | null>(null)

  async function loadRepositories() {
    try {
      setError('')

      const response = await fetch('/api/v1/github/repositories')

      if (!response.ok) {
        throw new Error(`GitHub API failed: ${response.status}`)
      }

      const data: GitHubRepository[] = await response.json()
      setRepositories(data)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Unable to load repositories',
      )
    } finally {
      setLoading(false)
    }
  }

  async function synchronizeRepositories() {
    try {
      setSyncing(true)
      setSyncError('')
      setSyncResult(null)

      const response = await fetch('/api/v1/github/sync', {
        method: 'POST',
      })

      if (!response.ok) {
        throw new Error(`Synchronization failed: ${response.status}`)
      }

      const result: SyncResult = await response.json()
      setSyncResult(result)

      // Refresh repository data after synchronization.
      await loadRepositories()
    } catch (err) {
      setSyncError(
        err instanceof Error
          ? err.message
          : 'Unable to synchronize repositories',
      )
    } finally {
      setSyncing(false)
    }
  }

  useEffect(() => {
    void loadRepositories()
  }, [])

  const filteredRepositories = repositories.filter((repository) => {
    const query = search.toLowerCase().trim()

    return (
      repository.name.toLowerCase().includes(query) ||
      repository.full_name.toLowerCase().includes(query) ||
      (repository.description ?? '').toLowerCase().includes(query)
    )
  })

  function formatDate(value: string) {
    const date = new Date(value)

    if (Number.isNaN(date.getTime())) {
      return 'Unknown'
    }

    return date.toLocaleDateString(undefined, {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    })
  }

  return (
    <div className="repositories-page">
      <section className="page-heading">
        <div>
          <span className="eyebrow">GITHUB INTEGRATION</span>
          <h1>Repositories</h1>
          <p>
            Discover your GitHub repositories and synchronize
            service metadata into the catalogue.
          </p>
        </div>

        <button
          className="refresh-button"
          onClick={synchronizeRepositories}
          disabled={syncing}
        >
          <RefreshCw
            size={16}
            className={syncing ? 'spinning' : ''}
          />
          {syncing ? 'Synchronizing...' : 'Sync repositories'}
        </button>
      </section>

      <section className="repository-summary">
        <div className="repository-summary-card">
          <span className="repository-summary-icon">
            <GitBranch size={19} />
          </span>
          <div>
            <strong>{loading ? '—' : repositories.length}</strong>
            <span>Discovered repositories</span>
          </div>
        </div>

        <div className="repository-summary-card">
          <span className="repository-summary-icon">
            <GitFork size={19} />
          </span>
          <div>
            <strong>
              {loading
                ? '—'
                : repositories.filter((repo) => repo.fork).length}
            </strong>
            <span>Forked repositories</span>
          </div>
        </div>
      </section>

      {syncResult && (
        <section className="sync-result">
          <div className="sync-result-heading">
            <CheckCircle2 size={19} />
            <strong>Synchronization completed</strong>
          </div>

          <div className="sync-result-grid">
            <SyncMetric label="Scanned" value={syncResult.scanned} />
            <SyncMetric label="Imported" value={syncResult.imported} />
            <SyncMetric label="Updated" value={syncResult.updated} />
            <SyncMetric label="Skipped" value={syncResult.skipped} />
            <SyncMetric label="Invalid" value={syncResult.invalid} />
          </div>
        </section>
      )}

      {syncError && (
        <div className="error-banner">
          <AlertCircle size={16} />
          <span>{syncError}</span>
          <button onClick={synchronizeRepositories}>Try again</button>
        </div>
      )}

      <section className="panel repository-panel">
        <div className="panel-heading">
          <div>
            <h2>GitHub repositories</h2>
            <p>
              {loading
                ? 'Loading repositories...'
                : `${filteredRepositories.length} of ${repositories.length} repositories`}
            </p>
          </div>

          <span className="catalogue-note">
            <GitBranch size={15} />
            Connected to GitHub
          </span>
        </div>

        <label className="catalogue-filter-search repository-search">
          <Search size={16} />
          <input
            type="search"
            placeholder="Search repositories..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </label>

        {loading ? (
          <div className="catalogue-message">
            Loading GitHub repositories...
          </div>
        ) : error ? (
          <div className="catalogue-message catalogue-error">
            <strong>Unable to load repositories.</strong>
            <span>{error}</span>
            <button onClick={() => void loadRepositories()}>
              Try again
            </button>
          </div>
        ) : filteredRepositories.length === 0 ? (
          <div className="catalogue-message">
            <GitBranch size={24} />
            <strong>No repositories found</strong>
            <span>Try a different search term.</span>
          </div>
        ) : (
          <div className="catalogue-table-wrapper">
            <table className="catalogue-table repository-table">
              <thead>
                <tr>
                  <th>Repository</th>
                  <th>Default branch</th>
                  <th>Type</th>
                  <th>Last updated</th>
                  <th>GitHub</th>
                </tr>
              </thead>

              <tbody>
                {filteredRepositories.map((repository) => (
                  <tr key={repository.full_name}>
                    <td>
                      <div className="service-name">
                        <strong>{repository.name}</strong>
                        <span>
                          {repository.description ||
                            repository.full_name}
                        </span>
                      </div>
                    </td>

                    <td>
                      <span className="branch-label">
                        <GitBranch size={14} />
                        {repository.default_branch}
                      </span>
                    </td>

                    <td>
                      <span className="repository-type">
                        {repository.fork ? 'Fork' : 'Source'}
                      </span>
                    </td>

                    <td>{formatDate(repository.updated_at)}</td>

                    <td>
                      <a
                        className="repository-link"
                        href={repository.html_url}
                        target="_blank"
                        rel="noreferrer"
                      >
                        Open
                        <ExternalLink size={14} />
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <p className="repository-hint">
        Synchronization reads catalog-info.yaml files from your
        repositories and imports valid service metadata into the catalogue.
      </p>
    </div>
  )
}

function SyncMetric({
  label,
  value,
}: {
  label: string
  value: number
}) {
  return (
    <div className="sync-metric">
      <strong>{value}</strong>
      <span>{label}</span>
    </div>
  )
}
