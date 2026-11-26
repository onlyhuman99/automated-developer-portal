
import { useEffect, useState, type ReactNode } from 'react'
import {
  Activity,
  Bell,
  Box,
  ChevronDown,
  CircleHelp,
  Code2,
  GitBranch,
  LayoutDashboard,
  Search,
  Settings,
  Users,
  Layers3,
  RefreshCw,
  ExternalLink,
} from 'lucide-react'
import './App.css'

type Statistics = {
  totalServices: number
  servicesByLifecycle: Record<string, number>
  servicesByOwner: Record<string, number>
}

type CatalogService = {
  id: string
  name: string
  description: string | null
  repositoryUrl: string
  owner: string
  lifecycle: 'PRODUCTION' | 'EXPERIMENTAL' | 'DEPRECATED'
}

function App() {
  const [statistics, setStatistics] = useState<Statistics | null>(null)
  const [services, setServices] = useState<CatalogService[]>([])

  const [loading, setLoading] = useState(true)
  const [catalogueLoading, setCatalogueLoading] = useState(true)

  const [error, setError] = useState('')
  const [catalogueError, setCatalogueError] = useState('')

  const [searchQuery, setSearchQuery] = useState('')
  const [lifecycleFilter, setLifecycleFilter] = useState('ALL')

  async function loadStatistics() {
    try {
      setError('')

      const response = await fetch('/api/v1/catalog/statistics')

      if (!response.ok) {
        throw new Error(`Statistics API failed: ${response.status}`)
      }

      const data: Statistics = await response.json()
      setStatistics(data)
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'Unable to load statistics',
      )
    } finally {
      setLoading(false)
    }
  }

  async function loadServices() {
    try {
      setCatalogueError('')

      const response = await fetch('/api/v1/services')

      if (!response.ok) {
        throw new Error(`Catalogue API failed: ${response.status}`)
      }

      const data: CatalogService[] = await response.json()
      setServices(data)
    } catch (err) {
      setCatalogueError(
        err instanceof Error ? err.message : 'Unable to load services',
      )
    } finally {
      setCatalogueLoading(false)
    }
  }

  function refreshDashboard() {
    setLoading(true)
    setCatalogueLoading(true)

    void Promise.all([loadStatistics(), loadServices()])
  }

  useEffect(() => {
    refreshDashboard()
  }, [])

  const production = statistics?.servicesByLifecycle?.PRODUCTION ?? 0
  const experimental = statistics?.servicesByLifecycle?.EXPERIMENTAL ?? 0
  const deprecated = statistics?.servicesByLifecycle?.DEPRECATED ?? 0

  const ownerEntries = Object.entries(statistics?.servicesByOwner ?? {})

  const maxOwnerCount = Math.max(
    1,
    ...ownerEntries.map(([, count]) => count),
  )

  const filteredServices = services.filter((service) => {
    const query = searchQuery.toLowerCase().trim()

    const matchesSearch =
      !query ||
      service.name.toLowerCase().includes(query) ||
      (service.description ?? '').toLowerCase().includes(query) ||
      service.owner.toLowerCase().includes(query)

    const matchesLifecycle =
      lifecycleFilter === 'ALL' ||
      service.lifecycle === lifecycleFilter

    return matchesSearch && matchesLifecycle
  })

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">
            <Layers3 size={21} />
          </div>
          <span>DevPortal</span>
        </div>

        <div className="workspace">
          <div className="workspace-avatar">A</div>
          <div className="workspace-details">
            <strong>Acme Engineering</strong>
            <span>Personal workspace</span>
          </div>
          <ChevronDown size={16} />
        </div>

        <div className="nav-section">
          <span className="nav-heading">WORKSPACE</span>

          <a className="nav-link active" href="#overview">
            <LayoutDashboard size={18} />
            <span>Overview</span>
          </a>

          <a className="nav-link" href="#catalogue">
            <Box size={18} />
            <span>Service catalogue</span>
          </a>

          <a className="nav-link" href="#repositories">
            <GitBranch size={18} />
            <span>Repositories</span>
          </a>

          <a className="nav-link" href="#teams">
            <Users size={18} />
            <span>Teams & ownership</span>
          </a>
        </div>

        <div className="nav-section">
          <span className="nav-heading">PREFERENCES</span>

          <a className="nav-link" href="#activity">
            <Activity size={18} />
            <span>Activity</span>
          </a>

          <a className="nav-link" href="#settings">
            <Settings size={18} />
            <span>Settings</span>
          </a>
        </div>

        <div className="sidebar-bottom">
          <div className="help-card">
            <div className="help-icon">
              <CircleHelp size={18} />
            </div>
            <div>
              <strong>Need help?</strong>
              <span>View documentation</span>
            </div>
          </div>

          <div className="user-profile">
            <div className="user-avatar">P</div>
            <div className="user-details">
              <strong>Developer</strong>
              <span>Administrator</span>
            </div>
            <Settings size={17} />
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="breadcrumb">
            Workspace <span>/</span> <strong>Overview</strong>
          </div>

          <div className="topbar-actions">
            <label className="search-box">
              <Search size={17} />
              <input
                type="search"
                placeholder="Search services..."
                value={searchQuery}
                onChange={(event) => setSearchQuery(event.target.value)}
              />
              <kbd>⌘ K</kbd>
            </label>

            <button className="icon-button" aria-label="Notifications">
              <Bell size={19} />
              <span className="notification-dot" />
            </button>

            <div className="topbar-avatar">P</div>
          </div>
        </header>

        <div className="dashboard-content" id="overview">
          <section className="page-heading">
            <div>
              <span className="eyebrow">DEVELOPER WORKSPACE</span>
              <h1>Overview</h1>
              <p>
                Monitor your services, repositories, and engineering
                ownership in one place.
              </p>
            </div>

            <button
              className="refresh-button"
              onClick={refreshDashboard}
              disabled={loading || catalogueLoading}
            >
              <RefreshCw
                size={16}
                className={
                  loading || catalogueLoading ? 'spinning' : ''
                }
              />
              Refresh data
            </button>
          </section>

          {error && (
            <div className="error-banner">
              <span>{error}</span>
              <button onClick={() => void loadStatistics()}>
                Try again
              </button>
            </div>
          )}

          <section className="stats-grid">
            <StatCard
              label="Total services"
              value={statistics?.totalServices}
              loading={loading}
              icon={<Box size={19} />}
              description="Registered in catalogue"
            />

            <StatCard
              label="Production"
              value={statistics?.servicesByLifecycle?.PRODUCTION}
              loading={loading}
              icon={<Activity size={19} />}
              description="Live services"
              accent="green"
            />

            <StatCard
              label="Experimental"
              value={statistics?.servicesByLifecycle?.EXPERIMENTAL}
              loading={loading}
              icon={<Code2 size={19} />}
              description="Under development"
              accent="blue"
            />

            <StatCard
              label="Deprecated"
              value={statistics?.servicesByLifecycle?.DEPRECATED}
              loading={loading}
              icon={<Layers3 size={19} />}
              description="Marked for retirement"
              accent="orange"
            />
          </section>

          <section className="analytics-grid">
            <div className="panel">
              <div className="panel-heading">
                <div>
                  <h2>Lifecycle distribution</h2>
                  <p>Services grouped by lifecycle status</p>
                </div>
                <span className="panel-icon">
                  <Activity size={18} />
                </span>
              </div>

              <div className="lifecycle-list">
                <LifecycleRow
                  label="Production"
                  count={production}
                  total={statistics?.totalServices ?? 0}
                  color="green"
                />

                <LifecycleRow
                  label="Experimental"
                  count={experimental}
                  total={statistics?.totalServices ?? 0}
                  color="blue"
                />

                <LifecycleRow
                  label="Deprecated"
                  count={deprecated}
                  total={statistics?.totalServices ?? 0}
                  color="orange"
                />
              </div>
            </div>

            <div className="panel">
              <div className="panel-heading">
                <div>
                  <h2>Services by owner</h2>
                  <p>Distribution across engineering teams</p>
                </div>
                <span className="panel-icon">
                  <Users size={18} />
                </span>
              </div>

              {loading ? (
                <div className="empty-state">
                  Loading ownership data...
                </div>
              ) : ownerEntries.length === 0 ? (
                <div className="empty-state">
                  No ownership data available.
                </div>
              ) : (
                <div className="owner-list">
                  {ownerEntries.map(([owner, count]) => (
                    <div className="owner-row" key={owner}>
                      <div className="owner-label">
                        <span className="owner-dot" />
                        <span>{owner}</span>
                        <strong>{count}</strong>
                      </div>

                      <div className="progress-track">
                        <div
                          className="progress-fill"
                          style={{
                            width: `${(count / maxOwnerCount) * 100}%`,
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </section>

          <section
            className="panel catalogue-panel"
            id="catalogue"
          >
            <div className="catalogue-heading">
              <div className="panel-heading">
                <div>
                  <h2>Service catalogue</h2>
                  <p>
                    Your registered software components
                    {!catalogueLoading && ` · ${filteredServices.length} services`}
                  </p>
                </div>

                <span className="catalogue-note">
                  <Box size={15} />
                  Live catalogue data
                </span>
              </div>

              <div className="catalogue-filters">
                <label className="catalogue-filter-search">
                  <Search size={16} />
                  <input
                    type="search"
                    placeholder="Search name, description or owner..."
                    value={searchQuery}
                    onChange={(event) => setSearchQuery(event.target.value)}
                  />
                </label>

                <select
                  aria-label="Filter by lifecycle"
                  value={lifecycleFilter}
                  onChange={(event) =>
                    setLifecycleFilter(event.target.value)
                  }
                >
                  <option value="ALL">All lifecycles</option>
                  <option value="PRODUCTION">Production</option>
                  <option value="EXPERIMENTAL">Experimental</option>
                  <option value="DEPRECATED">Deprecated</option>
                </select>
              </div>
            </div>

            {catalogueLoading ? (
              <div className="catalogue-message">
                Loading services...
              </div>
            ) : catalogueError ? (
              <div className="catalogue-message catalogue-error">
                <strong>Unable to load the service catalogue.</strong>
                <span>{catalogueError}</span>
                <button onClick={() => void loadServices()}>
                  Try again
                </button>
              </div>
            ) : filteredServices.length === 0 ? (
              <div className="catalogue-message">
                <div className="placeholder-icon">
                  <Box size={24} />
                </div>
                <strong>
                  {services.length === 0
                    ? 'No services registered'
                    : 'No matching services'}
                </strong>
                <span>
                  {services.length === 0
                    ? 'Add a service through the catalogue API or synchronize your GitHub repositories.'
                    : 'Try changing your search or lifecycle filter.'}
                </span>
              </div>
            ) : (
              <div className="catalogue-table-wrapper">
                <table className="catalogue-table">
                  <thead>
                    <tr>
                      <th>Service</th>
                      <th>Owner</th>
                      <th>Lifecycle</th>
                      <th>Repository</th>
                    </tr>
                  </thead>

                  <tbody>
                    {filteredServices.map((service) => (
                      <tr key={service.id}>
                        <td>
                          <div className="service-name">
                            <strong>{service.name}</strong>
                            <span>
                              {service.description || 'No description provided'}
                            </span>
                          </div>
                        </td>

                        <td>
                          <span className="service-owner">
                            <Users size={14} />
                            {service.owner}
                          </span>
                        </td>

                        <td>
                          <span
                            className={`lifecycle-badge ${service.lifecycle.toLowerCase()}`}
                          >
                            {service.lifecycle.charAt(0) +
                              service.lifecycle.slice(1).toLowerCase()}
                          </span>
                        </td>

                        <td>
                          <a
                            className="repository-link"
                            href={service.repositoryUrl}
                            target="_blank"
                            rel="noreferrer"
                            aria-label={`Open ${service.name} repository`}
                          >
                            GitHub
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

          <footer className="dashboard-footer">
            <span>DevPortal · Developer workspace</span>
            <span>Data provided by your Spring Boot backend</span>
          </footer>
        </div>
      </main>
    </div>
  )
}

type StatCardProps = {
  label: string
  value?: number
  loading: boolean
  icon: ReactNode
  description: string
  accent?: string
}

function StatCard({
  label,
  value,
  loading,
  icon,
  description,
  accent = 'neutral',
}: StatCardProps) {
  return (
    <div className="stat-card">
      <div className="stat-card-top">
        <span>{label}</span>
        <span className={`stat-icon ${accent}`}>{icon}</span>
      </div>

      <div className="stat-value">
        {loading ? <span className="skeleton" /> : (value ?? 0)}
      </div>

      <span className="stat-description">{description}</span>
    </div>
  )
}

type LifecycleRowProps = {
  label: string
  count: number
  total: number
  color: string
}

function LifecycleRow({
  label,
  count,
  total,
  color,
}: LifecycleRowProps) {
  const percentage = total > 0 ? (count / total) * 100 : 0

  return (
    <div className="lifecycle-row">
      <div className="lifecycle-label">
        <span className={`status-dot ${color}`} />
        <span>{label}</span>
        <strong>{count}</strong>
      </div>

      <div className="progress-track">
        <div
          className={`progress-fill ${color}`}
          style={{ width: `${percentage}%` }}
        />
      </div>

      <span className="percentage">
        {percentage.toFixed(0)}%
      </span>
    </div>
  )
}

export default App
