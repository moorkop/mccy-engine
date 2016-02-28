check_var() {
  if [[ ! -v $1 || -z $(eval echo \$${1}) ]]; then
    echo "Missing environment variable $1"
    exit 1
  fi
}

