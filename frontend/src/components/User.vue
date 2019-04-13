<template>
  <div class="mx-auto container">
    <b-card :title="user.firstName + ' ' + user.lastName"
            :sub-title="user.email">
      <b-card-group>
        <b-card header="Requested Items"
                header-tag="header">
          <div v-if="items && items.length">
            <b-table responsive :items="items" :fields="item_fields">
              <template slot="buyer" slot-scope="data">
                <b-badge variant="warning" v-if="!data.value">All</b-badge>
                <b-badge variant="info" v-else-if="data.value.id !== user_id">
                  {{data.value.firstName}}
                </b-badge>
                <b-badge variant="danger" v-else>You</b-badge>
              </template>
              <template slot="list" slot-scope="data">
                <router-link :to="{ name: 'ShoppingList', params: { id: data.value.id } }">
                  {{data.value.name}}
                </router-link>
              </template>
              <template slot="bought" slot-scope="cell">
                <b-badge variant="success" v-if="cell.value">Bought</b-badge>
                <b-button variant="primary" @click="buyItem(cell.item.id)" v-else>Buy</b-button>
              </template>
            </b-table>
          </div>
          <div v-else>
            <p class="card-text">You don't have any requested items.</p>
          </div>
        </b-card>
        <b-card header="Households"
                header-tag="header">
          <div v-if="households && households.length">
            <b-table stacked :items="households" :fields="household_fields">
              <template slot="tenants" slot-scope="data">{{data.value.length}}</template>
              <template slot="lists" slot-scope="data">
                <router-link v-for="list in data.value" :key="list.id"
                             :to="{ name: 'ShoppingList', params: { id: list.id } }">
                  {{list.name}}
                </router-link>
              </template>
              <template slot="id" slot-scope="data">
                <router-link :to="{ name: 'Household', params: { id: data.value } }">
                  <b-button variant="primary">Check</b-button>
                </router-link>
              </template>
            </b-table>
          </div>
          <div v-else>
            <p class="card-text">You did not joined any households yet.</p>
            <b-button href="#" variant="primary" disabled>Join Household</b-button>
          </div>
        </b-card>
      </b-card-group>
    </b-card>
  </div>
</template>

<script>
  import services from '@/app-service'

  export default {
    name: "User",
    data() {
      return {
        user_id: Number(this.$route.params.id),
        user: null,
        households: null,
        household_fields: [
          "street", "buildingNumber", "zipCode", "state", "tenants", "lists",
          {
            key: "id",
            label: "Check"
          }
        ],
        items: null,
        item_fields: [
          "name", "quantity", "buyer", "list", {
            key: "bought", label: "Buy"
          }
        ]
      };
    },

    created() {
      this.getData();
    },

    methods: {
      async getData() {
        this.user = this.$store.state.user;
        this.households = await services.getUserHouseholds(this.user_id);
        this.items = this.households.map(h => h.lists).flat().map(l => l.shoppingItems).flat();
      },
      sleep(ms) {
        return new Promise(resolve => {setTimeout(resolve, ms)});
      },
      async buyItem(id) {
        if (services.isDev()) {
          let item = this.items.find(i => i.id === id);
          item.bought = true;
        } else {
          await services.buyShoppingItem(id);
          await this.getData();
        }
      }
    }
  }
</script>

<style scoped>
</style>
